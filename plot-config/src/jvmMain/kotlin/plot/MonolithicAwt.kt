/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.plot.MonolithicCommon.PlotBuildInfo
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Error
import jetbrains.datalore.plot.MonolithicCommon.PlotsBuildResult.Success
import jetbrains.datalore.plot.config.FailureHandler
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
        var liveMapControl: JavafxCanvasControl? = null

        val resultComponent = if (plotContainer.liveMapFigures.isEmpty()) {

            plotComponent
        } else {
            val liveMapFigure = plotContainer.liveMapFigures.single()

            liveMapControl = JavafxCanvasControl(
                liveMapFigure.dimension().get().toVector(),
                1.0
            )

            Platform.runLater{ liveMapFigure.mapToCanvas(liveMapControl) }

            liveMapControl.component.bounds = Rectangle(0,0, liveMapControl.size.x, liveMapControl.size.y)
            plotComponent.bounds = Rectangle(0,0, plotInfo.size.get().x.toInt(), plotInfo.size.get().y.toInt())

            JFXPanel().also {
                it.add(plotComponent)
                it.add(liveMapControl.component)
            }
        }

        plotComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent) {
                super.mouseExited(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
                }

                liveMapControl?.dispatch(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
            }

            override fun mousePressed(e: MouseEvent) {
                super.mousePressed(e)
                liveMapControl?.dispatch(MouseEventSpec.MOUSE_PRESSED, AwtEventUtil.translate(e))
            }

            override fun mouseReleased(e: MouseEvent) {
                super.mouseReleased(e)
                liveMapControl?.dispatch(MouseEventSpec.MOUSE_RELEASED, AwtEventUtil.translate(e))
            }

            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)
                if (e.clickCount % 2 == 1) {
                    liveMapControl?.dispatch(MouseEventSpec.MOUSE_CLICKED, AwtEventUtil.translate(e))
                } else {
                    liveMapControl?.dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, AwtEventUtil.translate(e))
                }
            }
        })

        plotComponent.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                }

                liveMapControl?.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
            }

            override fun mouseDragged(e: MouseEvent) {
                super.mouseDragged(e)
                liveMapControl?.dispatch(MouseEventSpec.MOUSE_DRAGGED, AwtEventUtil.translate(e))
            }
        })


        return resultComponent
    }

    private fun DoubleVector.toVector(): Vector {
        return Vector(x.toInt(), y.toInt())
    }

    private fun createErrorLabel(s: String): JComponent {
        val label = JLabel(s)
        label.foreground = Color.RED
        return label
    }
}
