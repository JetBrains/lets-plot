/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapProviderUtil
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

//        val rootFigureBounds = toJBounds(svgRoot.bounds)
        val rootJComponentBounds = toJBounds(
            DoubleRectangle(DoubleVector.ZERO, svgRoot.bounds.dimension)
        )
        val rootFigureDim = rootJComponentBounds.size
        rootJPanel.preferredSize = rootFigureDim
        rootJPanel.minimumSize = rootFigureDim
        rootJPanel.maximumSize = rootFigureDim

        val rootJComponent: JComponent = svgComponentFactory(svgRoot.svg)
//        rootJComponent.bounds = rootFigureBounds
        rootJComponent.bounds = rootJComponentBounds
        rootJPanel.add(rootJComponent)

        //
        // Sub-plots
        //

        val elementJComponents = ArrayList<JComponent>()
        for (element in svgRoot.elements) {
            if (element is PlotSvgRoot) {
                val comp = processPlotFigure(element)
                comp.bounds = toJBounds(element.bounds)
                elementJComponents.add(comp)
            } else {
                val comp = processCompositeFigure(element as CompositeFigureSvgRoot)
                comp.bounds = toJBounds(element.bounds)
                elementJComponents.add(comp)
            }
        }

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
//                plotContainer,
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

            plotComponent.addMouseMotionListener(object : MouseAdapter() {
                override fun mouseMoved(e: MouseEvent) {
                    super.mouseMoved(e)
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

            return plotComponent
        }
    }
}