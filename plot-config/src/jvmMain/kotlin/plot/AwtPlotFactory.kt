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

abstract class AwtPlotFactory(
    val svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
    val executor: (() -> Unit) -> Unit
) {

    abstract fun buildPlotComponent(
        plotBuildInfo: MonolithicCommon.PlotBuildInfo
    ): JComponent

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = processSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(
                plotSpec, plotSize, computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, plotSize)
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return createErrorLabel(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }

            computationMessagesHandler(computationMessages)

            JPanel(null).apply {
                background = Color.WHITE

                for (plotInfo in success.buildInfos) {
                    val plotComponent = buildPlotComponent(plotInfo)
                    plotComponent.bounds = plotInfo.bounds().run {
                        Rectangle(
                            origin.x.toInt(),
                            origin.y.toInt(),
                            dimension.x.toInt(),
                            dimension.y.toInt()
                        )
                    }

                    add(plotComponent)
                }

                val dimensions = getPlotDimension(success.buildInfos)
                preferredSize = dimensions
                minimumSize = dimensions
                maximumSize = dimensions
            }

        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    private fun getPlotDimension(plotInfos: List<MonolithicCommon.PlotBuildInfo>): Dimension {
        return plotInfos
            .map { it.bounds() }
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }
            .run {
                Dimension(width.toInt(), height.toInt())
            }
    }

    fun buildPlotComponent(
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

    private fun handleException(e: RuntimeException): JComponent {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            LOG.error(e) {}
        }
        return createErrorLabel(failureInfo.message)
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