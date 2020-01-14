/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.server.config.PlotConfigServerSide


object MonolithicCommon {
    private val DEF_PLOT_SIZE = DoubleVector(500.0, 400.0)
    private val DEF_LIVE_MAP_SIZE = DoubleVector(800.0, 600.0)

    fun buildPlotsFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?
    ): PlotsBuildResult {
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        val processedSpec =
            if (PlotConfig.isFailure(plotSpec)) {
                plotSpec
            } else {
                PlotConfigServerSide.processTransform(plotSpec)
            }
        return buildPlotsFromProcessedSpecsIntern(
            processedSpec,
            plotSize
        )
    }

    private fun buildPlotsFromProcessedSpecsIntern(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?
    ): PlotsBuildResult {
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            return PlotsBuildResult.Error(errorMessage)
        }

        return when {
            PlotConfig.isPlotSpec(plotSpec) -> {
                PlotsBuildResult.Success(
                    listOf(buildSinglePlotFromProcessedSpecs(plotSpec, plotSize))
                )
            }
            PlotConfig.isGGBunchSpec(plotSpec) -> buildGGBunchFromProcessedSpecs(plotSpec)
            else -> throw RuntimeException("Unexpected plot spec kind: " + PlotConfig.specKind(plotSpec))
        }
    }

    private fun buildGGBunchFromProcessedSpecs(
        bunchSpec: MutableMap<String, Any>
    ): PlotsBuildResult {
        val bunchConfig = BunchConfig(bunchSpec)
        if (bunchConfig.bunchItems.isEmpty()) return PlotsBuildResult.Error("No plots in the bunch")

        val buildInfos = ArrayList<PlotBuildInfo>()
        var bunchBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        for (bunchItem in bunchConfig.bunchItems) {
            val plotSpec = bunchItem.featureSpec as MutableMap<String, Any>

            val plotSize = if (bunchItem.hasSize()) {
                bunchItem.size
            } else {
                PlotConfigClientSideUtil.getPlotSizeOrNull(plotSpec) ?: DEF_PLOT_SIZE
            }

            var buildInfo = buildSinglePlotFromProcessedSpecs(plotSpec, plotSize)

            buildInfo = PlotBuildInfo(
                buildInfo.plotContainer,
                DoubleVector(bunchItem.x, bunchItem.y),  // true origin
                buildInfo.size,
                buildInfo.computationMessages
            )
            buildInfos.add(buildInfo)

//            bunchBounds = bunchBounds.union(success.bounds())
        }

        return PlotsBuildResult.Success(buildInfos)
    }


    private fun buildSinglePlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?
    ): PlotBuildInfo {

        val computationMessages = ArrayList<String>()
        val assembler = createPlotAssembler(plotSpec) {
            computationMessages.addAll(it)
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
        val preferredSize = ValueProperty(plotSize)
        val plotContainer = PlotContainer(plot, preferredSize)
        return PlotBuildInfo(
            plotContainer,
            DoubleVector.ZERO,
            preferredSize,
            computationMessages
        )
    }

    private fun createPlotAssembler(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): PlotAssembler {

        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigClientSide.processTransform(plotSpec)
        val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
        if (computationMessages.isNotEmpty()) {
            computationMessagesHandler(computationMessages)
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


    sealed class PlotsBuildResult {
        val isError: Boolean = this is Error

        class Error(val error: String) : PlotsBuildResult()

        class Success(
            val buildInfos: List<PlotBuildInfo>
        ) : PlotsBuildResult()
    }

    class PlotBuildInfo(
        val plotContainer: PlotContainer,
        val origin: DoubleVector,
        val size: ReadableProperty<DoubleVector>,
        val computationMessages: List<String>
    ) {
        fun bounds(): DoubleRectangle {
            return DoubleRectangle(origin, size.get())
        }
    }
}