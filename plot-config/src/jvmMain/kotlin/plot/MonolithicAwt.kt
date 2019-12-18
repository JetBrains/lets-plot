/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import jetbrains.datalore.vis.svg.SvgSvgElement
import mu.KotlinLogging
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

private val LOG = KotlinLogging.logger {}

private val DEF_PLOT_SIZE = DoubleVector(500.0, 400.0)
private val DEF_LIVE_MAP_SIZE = DoubleVector(800.0, 600.0)

object MonolithicAwt {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        return try {
            PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
            val processedSpec = if (PlotConfig.isFailure(plotSpec)) {
                plotSpec
            } else {
                PlotConfigServerSide.processTransform(plotSpec)
            }
            buildPlotFromProcessedSpecsIntern(processedSpec, plotSize, componentFactory, executor)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        return try {
            buildPlotFromProcessedSpecsIntern(plotSpec, plotSize, componentFactory, executor)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    private fun buildPlotFromProcessedSpecsIntern(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {
        throwTestingErrors()  // noop

        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            return showError(errorMessage)
        }

        return when {
            PlotConfig.isPlotSpec(plotSpec) -> buildSinglePlotFromProcessedSpecs(
                plotSpec,
                plotSize,
                componentFactory,
                executor
            )
            PlotConfig.isGGBunchSpec(plotSpec) -> {
                //            buildGGBunchFromProcessedSpecs(plotSpec, parentElement)
                UNSUPPORTED()
            }
            else -> throw RuntimeException("Unexpected plot spec kind: " + PlotConfig.specKind(plotSpec))
        }
    }

    private fun buildSinglePlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {

        val assembler = createPlotAssembler(plotSpec) { messages ->
            messages.forEach {
                showInfo(it)
            }
        }

        // Figure out plot size
        @Suppress("NAME_SHADOWING")
        val plotSize = if (plotSize != null) {
            plotSize
        } else {
            var plotSizeSpec = PlotConfigClientSideUtil.getPlotSizeOrNull(plotSpec)
            if (plotSizeSpec != null) {
                plotSizeSpec
            } else {
                defaultPlotSize(assembler)
            }
        }

        val plot = assembler.createPlot()
        val plotContainer = PlotContainer(plot, ValueProperty(plotSize))
        return buildPlotSvgComponent(plotContainer, componentFactory, executor)
    }

    private fun createPlotAssembler(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): PlotAssembler {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigClientSide.processTransform(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        return PlotConfigClientSideUtil.createPlotAssembler(plotSpec)
    }

    private fun defaultPlotSize(assembler: PlotAssembler): DoubleVector {
        var plotSize = DEF_PLOT_SIZE
        val facets = assembler.facets
        if (facets.isDefined) {
            val xLevels = facets.xLevels!!
            val yLevels = facets.yLevels!!
            val columns = if (xLevels.isEmpty()) 1 else xLevels.size
            val rows = if (yLevels.isEmpty()) 1 else yLevels.size
            val panelWidth = DEF_PLOT_SIZE.x * (0.5 + 0.5 / columns)
            val panelHeight = DEF_PLOT_SIZE.y * (0.5 + 0.5 / rows)
            plotSize = DoubleVector(panelWidth * columns, panelHeight * rows)
        } else if (assembler.containsLiveMap) {
            plotSize = DEF_LIVE_MAP_SIZE
        }
        return plotSize
    }

    fun buildPlotSvgComponent(
        plotContainer: PlotContainer,
        componentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit
    ): JComponent {

        plotContainer.ensureContentBuilt()
        val component = componentFactory(plotContainer.svg)

        component.addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent) {
                super.mouseExited(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
                }
            }
        })
        component.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                super.mouseMoved(e)
                executor {
                    plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                }
            }
        })

        // TODO: Inject Livemap
//        plotContainer.liveMapFigures.forEach { liveMapFigure ->
//            val canvasControl =
//                DomCanvasControl(liveMapFigure.dimension().get().toVector())
//            liveMapFigure.mapToCanvas(canvasControl)
//            eventTarget.appendChild(canvasControl.rootElement)
//        }

        return component;
    }

    private fun DoubleVector.toVector(): Vector {
        return Vector(x.toInt(), y.toInt())
    }

    private fun handleException(e: RuntimeException): JComponent {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            LOG.error(e) {}
        }
        return showError(failureInfo.message)
    }

    private fun showError(message: String): JComponent {
        UNSUPPORTED()
    }

    private fun showInfo(message: String): JComponent {
        UNSUPPORTED()
    }


    private fun throwTestingErrors() {
        // testing errors
//        throw RuntimeException()
//        throw RuntimeException("My sudden crush")
//        throw IllegalArgumentException("User configuration error")
//        throw IllegalStateException("User configuration error")
//        throw IllegalStateException()   // Huh?
    }
}
