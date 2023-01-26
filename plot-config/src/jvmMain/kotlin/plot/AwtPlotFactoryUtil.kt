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
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapProviderUtil
import jetbrains.datalore.plot.server.config.BackendSpecTransformUtil
import jetbrains.datalore.vis.svg.SvgSvgElement
import mu.KotlinLogging
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel

private val LOG = KotlinLogging.logger {}

internal object AwtPlotFactoryUtil {

    private fun buildPlotComponent(
        figureBuilder: FigureBuildInfo,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        var cursorServiceConfig: Any? = null
        if (figureBuilder.containsLiveMap) {
            cursorServiceConfig = CursorServiceConfig()
            LiveMapProviderUtil.injectLiveMapProvider(
                figureBuilder,
                cursorServiceConfig
            )
        }

        val plot = figureBuilder.createFigure()
        val plotContainer = PlotContainer(plot, figureBuilder.bounds.dimension)
        val plotComponent = buildPlotComponent(plotContainer, svgComponentFactory, executor)
        return if (plot.containsLiveMap) {
            AwtLiveMapPanel(
                plotContainer,
                plotComponent,
                executor,
                cursorServiceConfig as CursorServiceConfig
            )

        } else {
            plotComponent
        }
    }

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = processSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth,
                svgComponentFactory,
                executor,
                computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth,
                plotPreferredWidth = null
            )
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return createErrorLabel(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            if (success.buildInfos.size == 1) {
                // a single plot
                return buildPlotComponent(
                    success.buildInfos[0],
                    svgComponentFactory, executor
                )
            }
            // ggbunch
            return buildGGBunchComponent(
                success.buildInfos,
                svgComponentFactory, executor
            )

        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    private fun buildGGBunchComponent(
        plotInfos: List<FigureBuildInfo>,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {

        val bunchComponent = DisposableJPanel(null)

        bunchComponent.border = null
//        bunchComponent.background = Colors.parseColor(Defaults.BACKDROP_COLOR).let {
//            Color(
//                it.red,
//                it.green,
//                it.blue,
//                it.alpha
//            )
//        }
        bunchComponent.isOpaque = false

        for (plotInfo in plotInfos) {
            val plotComponent = buildPlotComponent(
                plotInfo,
                svgComponentFactory, executor
            )
            val bounds = plotInfo.bounds
            plotComponent.bounds = Rectangle(
                bounds.origin.x.toInt(),
                bounds.origin.y.toInt(),
                bounds.dimension.x.toInt(),
                bounds.dimension.y.toInt()
            )
            bunchComponent.add(plotComponent)
        }

        val bunchBounds = plotInfos.map { it.bounds }
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
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
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
                BackendSpecTransformUtil.processTransform(plotSpec)
            }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Frontend transforms
        return PlotConfigClientSide.processTransform(plotSpec)
    }


    fun buildPlotComponent(
        plotContainer: PlotContainer,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        plotContainer.ensureContentBuilt()
        val svg = plotContainer.svg

        val plotComponent: JComponent = svgComponentFactory(svg)

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