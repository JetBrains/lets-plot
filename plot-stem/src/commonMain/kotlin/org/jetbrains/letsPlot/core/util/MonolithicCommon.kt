/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.config.BunchConfig
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString
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
                    buildCompositeFigureFromProcessedSpecs(
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
        plotPreferredWidth: Double?,
        compositeFigureComputationMessages: MutableList<String>? = null
    ): PlotFigureBuildInfo {
        val computationMessages = ArrayList<String>()
        val config = PlotConfigFrontend.create(plotSpec) {
            computationMessages.addAll(it)
        }

        val ownComputationMessages = compositeFigureComputationMessages?.let {
            // Rerout all messages upstream to the root composite figure.
            it.addAll(computationMessages)
            emptyList()
        } ?: computationMessages

        return buildSinglePlot(
            config,
            plotSize,
            plotMaxWidth, plotPreferredWidth,
            ownComputationMessages
        )
    }

    private fun buildSinglePlot(
        config: PlotConfigFrontend,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        plotPreferredWidth: Double?,
        computationMessages: List<String>
    ): PlotFigureBuildInfo {

        val preferredSize = PlotSizeHelper.singlePlotSize(
            config.toMap(),
            plotSize,
            plotMaxWidth,
            plotPreferredWidth,
            config.facets,
            config.containsLiveMap
        )

        val assembler = createPlotAssembler(config)
        return PlotFigureBuildInfo(
            assembler,
            config.toMap(),
            DoubleRectangle(DoubleVector.ZERO, preferredSize),
            computationMessages
        )
    }

    private fun buildCompositeFigureFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        plotPreferredWidth: Double?
    ): CompositeFigureBuildInfo {
        val computationMessages = ArrayList<String>()
        val compositeFigureConfig = CompositeFigureConfig(plotSpec) {
            computationMessages.addAll(it)
        }

        val preferredSize = PlotSizeHelper.compositeFigureSize(
            compositeFigureConfig,
            plotSize,
            plotMaxWidth,
            plotPreferredWidth,
        )

        return buildCompositeFigure(
            compositeFigureConfig,
            preferredSize,
            computationMessages
        )
    }

    private fun buildCompositeFigure(
        compositeFigureConfig: CompositeFigureConfig,
        preferredSize: DoubleVector,
        computationMessages: MutableList<String>,
    ): CompositeFigureBuildInfo {

        val elements: List<FigureBuildInfo?> = compositeFigureConfig.elementConfigs.map {
            it?.let {
                when (PlotConfig.figSpecKind(it)) {
//                    FigKind.PLOT_SPEC -> buildSinglePlotFromProcessedSpecs(
//                        plotSpec = it.toMap(),
                    FigKind.PLOT_SPEC -> buildSinglePlot(
                        config = it as PlotConfigFrontend,
                        plotSize = null,           // Will be updateed by sub-plots layout.
                        plotMaxWidth = null,
                        plotPreferredWidth = null,
//                        compositeFigureComputationMessages = computationMessages
                        computationMessages = emptyList()  // No "own messages" when a part of a composite.
                    )

                    FigKind.SUBPLOTS_SPEC -> {
                        val gridOptions = it as CompositeFigureConfig
                        buildCompositeFigure(
                            gridOptions,
                            preferredSize = DoubleVector.ZERO, // Will be updateed by sub-plots layout.
                            computationMessages
                        )
                    }

                    FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                }
            }
        }

        return CompositeFigureBuildInfo(
            elements = elements,
            layout = compositeFigureConfig.createLayout(),
            DoubleRectangle(DoubleVector.ZERO, preferredSize),
            computationMessages
        )
    }

    private fun createPlotAssembler(config: PlotConfigFrontend): PlotAssembler {
        return PlotConfigFrontendUtil.createPlotAssembler(config)
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
            SpecTransformBackendUtil.processTransform(plotSpec)
        }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // "Frontend" transforms.
        return PlotConfigFrontend.processTransform(plotSpec)
    }


    sealed class PlotsBuildResult {
        val isError: Boolean = this is Error

        class Error(val error: String) : PlotsBuildResult()

        class Success(
            val buildInfos: List<FigureBuildInfo>
        ) : PlotsBuildResult()
    }
}