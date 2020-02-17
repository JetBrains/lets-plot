/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.awt.svgToString.SvgToString
import mu.KotlinLogging
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

private val LOG = KotlinLogging.logger {}

object MonolithicAwt {

    fun buildSvgImagesFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): List<String> {
        val buildResult = MonolithicCommon.buildPlotsFromRawSpecs(plotSpec, plotSize)
        if (buildResult.isError) {
            val errorMessage = (buildResult as Error).error
            throw RuntimeException(errorMessage)
        }

        val success = buildResult as Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        if (computationMessages.isNotEmpty()) {
            computationMessagesHandler(computationMessages)
        }

        return success.buildInfos.map {
            it.plotContainer.ensureContentBuilt()
            it.plotContainer.svg
        }.map { SvgToString.render(it) }
    }

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            val buildResult = MonolithicCommon.buildPlotsFromRawSpecs(plotSpec, plotSize)
            if (buildResult.isError) {
                val errorMessage = (buildResult as Error).error
                return createErrorLabel(errorMessage)
            }

            val success = buildResult as Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            if (success.buildInfos.size == 1) {
                // a single plot
                return buildPlotSvgComponent(success.buildInfos[0], componentFactory, executor)
            }
            // ggbunch
            return buildGGBunchComponent(success.buildInfos, componentFactory, executor)

        } catch (e: RuntimeException) {
            val failureInfo = FailureHandler.failureInfo(e)
            if (failureInfo.isInternalError) {
                LOG.error(e) {}
            }
            createErrorLabel(failureInfo.message)
        }
    }

    private fun buildGGBunchComponent(
        plotInfos: List<PlotBuildInfo>,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {

        val bunchComponent = JPanel(null)
        bunchComponent.border = null

        for (plotInfo in plotInfos) {
            val plotComponent = buildPlotSvgComponent(plotInfo, componentFactory, executor)
            val bounds = plotInfo.bounds()
            plotComponent.bounds = Rectangle(
                bounds.origin.x.toInt(),
                bounds.origin.y.toInt(),
                bounds.dimension.x.toInt(),
                bounds.dimension.y.toInt()
            )
            bunchComponent.add(plotComponent)
        }

        val bunchBounds = plotInfos.map { it.bounds() }
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }

        val bunchDimensions = Dimension(
            bunchBounds.width.toInt(),
            bunchBounds.height.toInt()
        )

        bunchComponent.preferredSize = bunchDimensions
        bunchComponent.minimumSize = bunchDimensions
        bunchComponent.maximumSize = bunchDimensions
        return bunchComponent
    }

    fun buildPlotSvgComponent(
        plotInfo: PlotBuildInfo,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        val plotContainer = plotInfo.plotContainer
        plotContainer.ensureContentBuilt()

        val plotComponent: JComponent = componentFactory(plotContainer.svg)

        val controls = ArrayList<JavafxCanvasControl>()

        val resultComponent = if (plotContainer.liveMapFigures.isEmpty()) {

            plotComponent
        } else {
            plotComponent.bounds = Rectangle(0,0, plotInfo.size.get().x.toInt(), plotInfo.size.get().y.toInt())
            val panel = JFXPanel()

            panel.add(plotComponent)

            plotContainer.liveMapFigures.forEach { canvasFigure ->
                val canvasBounds = canvasFigure.bounds().get()
                val rootGroup = Group()

                JFXPanel()
                    .apply {
                        scene = Scene(rootGroup)
                        bounds = Rectangle(
                            canvasBounds.origin.x,
                            canvasBounds.origin.y,
                            canvasBounds.dimension.x,
                            canvasBounds.dimension.y
                        )
                        panel.add(this)
                    }

                JavafxCanvasControl(
                    rootGroup,
                    canvasBounds.dimension,
                    1.0,
                    AwtEventPeer(plotComponent, canvasBounds)
                ).let {
                    Platform.runLater{ canvasFigure.mapToCanvas(it) }
                    controls.add(it)
                }
            }

            panel
        }

        plotComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent) {
                super.mouseExited(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
                }
            }
        })

        plotComponent.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                }
            }
        })


        return resultComponent
    }

    private fun createErrorLabel(s: String): JComponent {
        val label = JLabel(s)
        label.foreground = Color.RED
        return label
    }
}
