/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.config.FailureHandler
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.server.config.PlotConfigClientSideJvmJs
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import jetbrains.datalore.vis.svg.SvgSvgElement
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

abstract class PlotFactory(
    val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    val executor: (() -> Unit) -> Unit
) {

    abstract fun buildPlotComponent(
        plotBuildInfo: MonolithicCommon.PlotBuildInfo,
        buildPlotSvgComponent: (plotContainer: PlotContainer) -> JComponent
    ) : JComponent

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {

            @Suppress("NAME_SHADOWING")
            val plotSpec = processSpecs(plotSpec, frontendOnly = false)
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, plotSize)
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return createErrorLabel(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            if (success.buildInfos.size == 1) {
                // a single plot
                return buildPlotComponent(success.buildInfos[0], ::buildPlotSvgComponent)
            }
            // ggbunch
            return buildGGBunchComponent(success.buildInfos)

        } catch (e: RuntimeException) {
            val failureInfo = FailureHandler.failureInfo(e)
            if (failureInfo.isInternalError) {
                LOG.error(e) {}
            }
            createErrorLabel(failureInfo.message)
        }
    }

    fun buildPlotSvgComponent(
        plotContainer: PlotContainer
    ): JComponent {
        plotContainer.ensureContentBuilt()

        val plotComponent: JComponent = svgComponentFactory(plotContainer.svg)

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

        return plotComponent
    }


    private fun buildGGBunchComponent(
        plotInfos: List<MonolithicCommon.PlotBuildInfo>
    ): JComponent {

        val bunchComponent = JPanel(null)
        bunchComponent.border = null

        for (plotInfo in plotInfos) {
            val plotComponent = buildPlotComponent(plotInfo, ::buildPlotSvgComponent)
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

    private fun createErrorLabel(s: String): JComponent {
        val label = JLabel(s)
        label.foreground = Color.RED
        return label
    }

    @Suppress("SameParameterValue")
    private fun processSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Backend transforms
        @Suppress("NAME_SHADOWING")
        val plotSpec =
            if (frontendOnly) {
                plotSpec
            } else {
                // This transform doesn't need to be "portable"
                // Could use PlotConfigServerSideJvm in case we needed "encoding"
                PlotConfigServerSide.processTransform(plotSpec)
            }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Frontend transforms
        return PlotConfigClientSideJvmJs.processTransform(plotSpec)
    }
}