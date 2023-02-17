/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.layout.figure.composite.CompositeFigureGridAlignmentLayout
import jetbrains.datalore.plot.builder.layout.figure.composite.CompositeFigureGridLayout
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.server.config.BackendSpecTransformUtil
import jetbrains.datalore.vis.svgToString.SvgToString
import kotlin.math.max

object MonolithicCommon {

    /**
     * Static SVG export
     */
    fun buildSvgImagesFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        svgToString: SvgToString,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): List<String> {
        @Suppress("NAME_SHADOWING")
        val plotSpec = processRawSpecs(plotSpec, frontendOnly = false)
        val buildResult = buildPlotsFromProcessedSpecs(plotSpec, plotSize)
        if (buildResult.isError) {
            val errorMessage = (buildResult as PlotsBuildResult.Error).error
            throw RuntimeException(errorMessage)
        }

        val success = buildResult as PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        if (computationMessages.isNotEmpty()) {
            computationMessagesHandler(computationMessages)
        }

        return success.buildInfos.map { buildInfo ->
            FigureToPlainSvg(buildInfo).eval()
        }.map { svgToString.render(it) }
    }


    fun buildPlotsFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double? = null,
        plotPreferredWidth: Double? = null
    ): PlotsBuildResult {
        throwTestingErrors()  // noop

        @Suppress("NAME_SHADOWING")
        val plotSize = plotSize?.let {
            // Fix error (Batik):
            //  org.apache.batik.bridge.BridgeException: null:-1
            //  The attribute "height" of the element <svg> cannot be negative
            DoubleVector(
                max(0.0, it.x),
                max(0.0, it.y)
            )
        }

        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            return PlotsBuildResult.Error(errorMessage)
        }

        return when (PlotConfig.figSpecKind(plotSpec)) {
            FigKind.PLOT_SPEC -> PlotsBuildResult.Success(
                listOf(
                    buildSinglePlotFromProcessedSpecs(
                        plotSpec,
                        plotSize,
                        plotMaxWidth,
                        plotPreferredWidth
                    )
                )
            )

            FigKind.SUBPLOTS_SPEC -> PlotsBuildResult.Success(
                listOf(
                    buildFigGridFromProcessedSpecs(
                        plotSpec,
                        plotSize,
                        plotMaxWidth,
                        plotPreferredWidth
                    )
                )
            )

            FigKind.GG_BUNCH_SPEC -> buildGGBunchFromProcessedSpecs(
                plotSpec,
                plotMaxWidth,
                plotPreferredWidth
            )
        }
    }

    private fun buildGGBunchFromProcessedSpecs(
        bunchSpec: MutableMap<String, Any>,
        maxWidth: Double?,
        preferredWidth: Double?
    ): PlotsBuildResult {

        val naturalSize = PlotSizeHelper.plotBunchSize(bunchSpec)
        val scaledSize = preferredWidth?.let { w ->
            naturalSize.mul(max(Defaults.MIN_PLOT_WIDTH, w) / naturalSize.x)
        } ?: naturalSize
        val neededSize = if (maxWidth != null && maxWidth < scaledSize.x) {
            scaledSize.mul(max(Defaults.MIN_PLOT_WIDTH, maxWidth) / scaledSize.x)
        } else {
            scaledSize
        }

        val scalingCoef = neededSize.x / naturalSize.x

        val bunchConfig = BunchConfig(bunchSpec)
        if (bunchConfig.bunchItems.isEmpty()) return PlotsBuildResult.Error(
            "No plots in the bunch"
        )

        val buildInfos = ArrayList<FigureBuildInfo>()
        for (bunchItem in bunchConfig.bunchItems) {
            val plotSpec = bunchItem.featureSpec as MutableMap<String, Any>
            val itemSize = PlotSizeHelper.bunchItemSize(bunchItem)
            val itemBounds = DoubleRectangle(
                DoubleVector(bunchItem.x, bunchItem.y).mul(scalingCoef),
                itemSize.mul(scalingCoef)
            )

            val plotFigureBuildInfo = buildSinglePlotFromProcessedSpecs(
                plotSpec,
                itemSize,
                plotMaxWidth = null,
                plotPreferredWidth = null
            ).withBounds(itemBounds)

            buildInfos.add(plotFigureBuildInfo)
        }

        return PlotsBuildResult.Success(buildInfos)
    }

    private fun buildSinglePlotFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        plotPreferredWidth: Double?
    ): PlotFigureBuildInfo {

        val computationMessages = ArrayList<String>()
        val config = PlotConfigClientSide.create(plotSpec) {
            computationMessages.addAll(it)
        }

        val preferredSize = PlotSizeHelper.singlePlotSize(
            plotSpec,
            plotSize,
            plotMaxWidth,
            plotPreferredWidth,
            config.facets,
            config.containsLiveMap
        )

        val assembler = createPlotAssembler(config)
        return PlotFigureBuildInfo(
            assembler,
            plotSpec,
            DoubleRectangle(DoubleVector.ZERO, preferredSize),
            computationMessages
        )
    }

    private fun buildFigGridFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        plotPreferredWidth: Double?
    ): CompositeFigureBuildInfo {
        // ToDo: collect computationMessages.
        val computationMessages = ArrayList<String>()
        val gridConfig = FigGridConfigClientSide(plotSpec) {
            computationMessages.addAll(it)
        }

        val preferredSize = PlotSizeHelper.subPlotsSize(
            plotSpec,
            plotSize,
            plotMaxWidth,
            plotPreferredWidth,
        )

        return buildFigureGrid(
            gridConfig,
            preferredSize
        )
    }

    private fun buildFigureGrid(
        gridConfig: FigGridConfigClientSide,
        preferredSize: DoubleVector,
    ): CompositeFigureBuildInfo {

        val gridElements: List<FigureBuildInfo?> = gridConfig.elementConfigs.map {
            it?.let {
                when (PlotConfig.figSpecKind(it)) {
                    FigKind.PLOT_SPEC -> buildSinglePlotFromProcessedSpecs(
                        plotSpec = it.toMap(),
                        plotSize = null,           // Will be updateed by subplots' layout.
                        plotMaxWidth = null,
                        plotPreferredWidth = null
                    )

                    FigKind.SUBPLOTS_SPEC -> {
                        val gridOptions = it as FigGridConfigClientSide
                        buildFigureGrid(
                            gridOptions,
                            preferredSize = DoubleVector.ZERO // Will be updateed by subplots' layout.
                        )
                    }

                    FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                }
            }
        }

        return CompositeFigureBuildInfo(
            elements = gridElements,
//            layout = CompositeFigureGridLayout(
            layout = CompositeFigureGridAlignmentLayout(
                ncol = gridConfig.ncol,
                nrow = gridConfig.nrow,
            ),
            DoubleRectangle(DoubleVector.ZERO, preferredSize),
        )
    }

    private fun createPlotAssembler(config: PlotConfigClientSide): PlotAssembler {
        return PlotConfigClientSideUtil.createPlotAssembler(config)
    }

    private fun throwTestingErrors() {
        // testing errors
//        throw RuntimeException()
//        throw RuntimeException("My sudden crush")
//        throw IllegalArgumentException("User configuration error")
//        throw IllegalStateException("User configuration error")
//        throw IllegalStateException()   // Huh?
    }

    /**
     * Applies all transformations to the plot specifications.
     * @param plotSpec: raw specifications of a single plot or GGBunch
     */
    fun processRawSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // "Backend" transforms.
        @Suppress("NAME_SHADOWING")
        val plotSpec = if (frontendOnly) {
            plotSpec
        } else {
            BackendSpecTransformUtil.processTransform(plotSpec)
        }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // "Frontend" transforms.
        return PlotConfigClientSide.processTransform(plotSpec)
    }


    sealed class PlotsBuildResult {
        val isError: Boolean = this is Error

        class Error(val error: String) : PlotsBuildResult()

        class Success(
            val buildInfos: List<FigureBuildInfo>
        ) : PlotsBuildResult()
    }
}