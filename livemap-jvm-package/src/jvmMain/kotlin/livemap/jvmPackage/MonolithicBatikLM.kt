/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.livemap.jvmPackage

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.AwtPlotFactory
import jetbrains.datalore.plot.DisposableJPanel
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapUtil
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.awt.AwtRepaintTimer
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.EventQueue.invokeLater
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel

object MonolithicBatikLM {

    private val mappers: MutableList<() -> Unit> = ArrayList()

    fun mapsToCanvas() {
        invokeLater {
            mappers.forEach { it() }
        }
    }

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return createPlotFactory(svgComponentFactory, executor)
            .buildPlotFromRawSpecs(plotSpec, plotSize, computationMessagesHandler)
    }

    private fun createPlotFactory(
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): AwtPlotFactory {
        return object : AwtPlotFactory(svgComponentFactory, executor) {
            override fun buildPlotComponent(
                plotBuildInfo: MonolithicCommon.PlotBuildInfo
            ): JComponent {
                val assembler = plotBuildInfo.plotAssembler

                val cursorServiceConfig = CursorServiceConfig()
                injectLiveMapProvider(assembler, plotBuildInfo.processedPlotSpec, cursorServiceConfig)

                val plot = assembler.createPlot()
                val plotContainer = PlotContainer(plot, plotBuildInfo.size)
                val plotComponent = buildPlotComponent(plotContainer)

                cursorServiceConfig.defaultSetter { plotComponent.cursor = Cursor.getDefaultCursor() }
                cursorServiceConfig.pointerSetter { plotComponent.cursor = Cursor(Cursor.HAND_CURSOR) }

                // Move tooltip when map moved
                plotComponent.addMouseMotionListener(object : MouseAdapter() {
                    override fun mouseDragged(e: MouseEvent) {
                        super.mouseDragged(e)
                        executor {
                            plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                        }
                    }
                })

                return if (plotContainer.liveMapFigures.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    buildPlotLiveMapComponent(
                        plotContainer.liveMapFigures as List<CanvasFigure>,
                        plotComponent,
                        plotBuildInfo.size.get()
                    )
                } else {
                    plotComponent
                }
            }
        }
    }

    private fun buildPlotLiveMapComponent(
        liveMapFigures: List<CanvasFigure>,
        plotComponent: JComponent,
        size: DoubleVector
    ): JComponent {
        val plotBounds = Rectangle(0,0, size.x.toInt(), size.y.toInt())

        plotComponent.bounds = plotBounds

        // Fix background color for jfx
        val panel = object : DisposableJPanel() {
            override fun paint(g: Graphics?) {
                g?.color = Color.WHITE
                g?.fillRect(0,0, this.width, this.height)
                super.paint(g)
            }
        }

        panel.add(plotComponent)

        val timer = AwtRepaintTimer(panel::repaint)

        liveMapFigures.forEach {canvasFigure ->
            val canvasBounds = canvasFigure.bounds().get()

            val layerPanel = JPanel()
                .apply {
                    bounds = Rectangle(
                        canvasBounds.origin.x,
                        canvasBounds.origin.y,
                        canvasBounds.dimension.x,
                        canvasBounds.dimension.y
                    )
                    panel.add(this)
                }

            AwtCanvasControl(
                layerPanel,
                canvasBounds.dimension,
                1.0,
                AwtEventPeer(plotComponent, canvasBounds),
                timer
            ).let {
                mappers.add {
                    canvasFigure.mapToCanvas(it)
                }
            }
        }

        // Fixed panel minimum size for scroll pane
        val plotDimensions = Dimension(
            plotBounds.width,
            plotBounds.height
        )

        panel.preferredSize = plotDimensions
        panel.minimumSize = plotDimensions
        panel.maximumSize = plotDimensions

        return panel
    }

    private fun injectLiveMapProvider(
        plotAssembler: PlotAssembler,
        processedPlotSpec: MutableMap<String, Any>,
        cursorServiceConfig: CursorServiceConfig
    ) {
        LiveMapOptionsParser.parseFromPlotSpec(processedPlotSpec)
            ?.let {
                LiveMapUtil.injectLiveMapProvider(
                    plotAssembler.layersByTile,
                    it,
                    cursorServiceConfig
                )
            }
    }
}