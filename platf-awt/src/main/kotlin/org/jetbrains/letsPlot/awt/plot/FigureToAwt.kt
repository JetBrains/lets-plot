/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.util.AwtEventUtil
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.interact.CompositeToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.core.plot.livemap.CursorServiceConfig
import org.jetbrains.letsPlot.core.plot.livemap.LiveMapProviderUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

internal class FigureToAwt(
    private val buildInfo: FigureBuildInfo,
    private val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    private val executor: (() -> Unit) -> Unit
) {

    fun eval(): JComponent {

        val buildInfo = buildInfo.layoutedByOuterSize()

        buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
            val cursorServiceConfig = CursorServiceConfig()
            LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
            cursorServiceConfig
        }

        val svgRoot = buildInfo.createSvgRoot()
        return if (svgRoot is CompositeFigureSvgRoot) {
            processCompositeFigure(svgRoot)
        } else {
            processPlotFigure(svgRoot as PlotSvgRoot)
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
    ): JComponent {

        svgRoot.ensureContentBuilt()

        // JPanel
        val rootJPanel = DisposableJPanel(null)
        rootJPanel.registerDisposable(
            object : Disposable {
                override fun dispose() {
                    svgRoot.clearContent()
                }
            }
        )

        rootJPanel.border = null
//        rootJPanel.background = Colors.parseColor(Defaults.BACKDROP_COLOR).let {
//            Color(
//                it.red,
//                it.green,
//                it.blue,
//                it.alpha
//            )
//        }
        rootJPanel.isOpaque = false

        fun toJBounds(from: DoubleRectangle): Rectangle {
            return Rectangle(
                from.origin.x.toInt(),
                from.origin.y.toInt(),
                from.dimension.x.toInt(),
                from.dimension.y.toInt()
            )
        }

        val rootJComponentBounds = toJBounds(
            DoubleRectangle(DoubleVector.ZERO, svgRoot.bounds.dimension)
        )
        val rootFigureDim = rootJComponentBounds.size
        rootJPanel.preferredSize = rootFigureDim

        val rootJComponent: JComponent = svgComponentFactory(svgRoot.svg)
        rootJComponent.bounds = rootJComponentBounds
        rootJPanel.add(rootJComponent)

        //
        // Sub-plots
        //

        val elementJComponents = ArrayList<JComponent>()
        val elementToolEventDispatchers = ArrayList<ToolEventDispatcher>()
        // In Swing components actually are painted in the reverse order of how they were added,
        // Thus, reverse elements here to have subplots painted in the order we need.
        for (element in svgRoot.elements.asReversed()) {
            val comp = if (element is PlotSvgRoot) {
                processPlotFigure(element)
            } else {
                processCompositeFigure(element as CompositeFigureSvgRoot)
            }
            comp.bounds = toJBounds(element.bounds)
            elementJComponents.add(comp)
            (comp.getClientProperty(ToolEventDispatcher::class) as? ToolEventDispatcher)?.let {
                elementToolEventDispatchers.add(it)
            }
        }

        val toolEventDispatcher = CompositeToolEventDispatcher(elementToolEventDispatchers)
        rootJPanel.putClientProperty(ToolEventDispatcher::class, toolEventDispatcher)

        elementJComponents.forEach {
//            rootJPanel.add(it)   // Do not!!!

            // Do not add everithing to root panel.
            // Instead, build components tree: rootPanel -> rootComp -> [subComp->[subSubComp,...], ...].
            // Otherwise JavaFX will not properly propogate mouse events.
            rootJComponent.add(it)
        }

        return rootJPanel
    }

    private fun processPlotFigure(
        svgRoot: PlotSvgRoot,
    ): JComponent {
        val plotContainer = PlotContainer(svgRoot)
        val plotComponent = buildSinglePlotComponent(plotContainer, svgComponentFactory, executor)

        return if (svgRoot.isLiveMap) {
            AwtLiveMapPanel(
                svgRoot.liveMapFigures,
                plotComponent,
                executor,
                svgRoot.liveMapCursorServiceConfig as CursorServiceConfig
            )

        } else {
            plotComponent
        }
    }


    companion object {
        private fun buildSinglePlotComponent(
            plotContainer: PlotContainer,
            svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
            executor: (() -> Unit) -> Unit
        ): JComponent {
            val svg = plotContainer.svg

            val plotComponent: JComponent = svgComponentFactory(svg)
            (plotComponent as DisposingHub).registerDisposable(plotContainer)
            plotComponent.putClientProperty(ToolEventDispatcher::class, plotContainer.toolEventDispatcher)
            (plotComponent as DisposingHub).registerDisposable(object : Disposable {
                override fun dispose() {
                    plotComponent.putClientProperty(ToolEventDispatcher::class, null)
                }
            })

            plotComponent.addMouseMotionListener(object : MouseAdapter() {
                private var lastEvent: MouseEvent? = null

                override fun mouseMoved(e: MouseEvent) {
                    super.mouseMoved(e)

                    // For some reason AWT sends two events with the same coord for one mouse move.
                    lastEvent?.let {
                        if (it.id == e.id && it.point == e.point) {
                            return
                        }
                    }

                    lastEvent = e

                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                    }
                }

                override fun mouseDragged(e: MouseEvent) {
                    super.mouseDragged(e)
                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_DRAGGED, AwtEventUtil.translate(e))
                    }
                }
            })

            plotComponent.addMouseListener(object : MouseAdapter() {
                override fun mouseExited(e: MouseEvent) {
                    super.mouseExited(e)
                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
                    }
                }

                override fun mouseClicked(e: MouseEvent) {
                    super.mouseClicked(e)
                    val event = if (e.clickCount % 2 == 1) {
                        MouseEventSpec.MOUSE_CLICKED
                    } else {
                        MouseEventSpec.MOUSE_DOUBLE_CLICKED
                    }

                    executor {
                        plotContainer.mouseEventPeer.dispatch(event, AwtEventUtil.translate(e))
                    }
                }

                override fun mousePressed(e: MouseEvent) {
                    super.mousePressed(e)
                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_PRESSED, AwtEventUtil.translate(e))
                    }
                }

                override fun mouseReleased(e: MouseEvent) {
                    super.mouseReleased(e)
                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_RELEASED, AwtEventUtil.translate(e))
                    }
                }

                override fun mouseEntered(e: MouseEvent) {
                    super.mouseEntered(e)
                    executor {
                        plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_ENTERED, AwtEventUtil.translate(e))
                    }
                }
            })

            plotComponent.addMouseWheelListener { e ->
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, AwtEventUtil.translate(e))
                }
            }

            return plotComponent
        }
    }
}