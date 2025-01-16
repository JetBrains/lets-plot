/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.CompositeFigureGridLayoutBase
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString

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
        val sizingPolicy = plotSize?.let { SizingPolicy.fixed(plotSize.x, plotSize.y) }
        val buildResult = buildPlotsFromProcessedSpecs(
            plotSpec,
            sizingPolicy
        )
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
        plotSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy?,
    ): PlotsBuildResult {
        throwTestingErrors()  // noop

        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            return PlotsBuildResult.Error(errorMessage)
        }

        return when (PlotConfig.figSpecKind(plotSpec)) {
            FigKind.PLOT_SPEC -> {
                PlotsBuildResult.Success(
                    listOf(
                        buildSinglePlotFromProcessedSpecs(
                            plotSpec,
                            sizingPolicy
                        )
                    )
                )
            }

            FigKind.SUBPLOTS_SPEC -> PlotsBuildResult.Success(
                listOf(
                    buildCompositeFigureFromProcessedSpecs(
                        plotSpec,
                        sizingPolicy
                    )
                )
            )

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
        }
    }

    private fun buildSinglePlotFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy?,
    ): PlotFigureBuildInfo {
        val computationMessages = ArrayList<String>()
        val config = PlotConfigFrontend.create(
            plotSpec,
            containerTheme = null
        ) {
            computationMessages.addAll(it)
        }

        return buildSinglePlot(
            config,
            sizingPolicy = sizingPolicy,
            sharedContinuousDomainX = null,  // only applicable to "composite figures"
            sharedContinuousDomainY = null,
            computationMessages
        )
    }

    private fun buildSinglePlot(
        config: PlotConfigFrontend,
        sizingPolicy: SizingPolicy?,
        sharedContinuousDomainX: DoubleSpan?,
        sharedContinuousDomainY: DoubleSpan?,
        computationMessages: List<String>
    ): PlotFigureBuildInfo {

        val preferredSize = PlotSizeHelper.singlePlotSize(
            plotSpec = config.toMap(),
            sizingPolicy = sizingPolicy,
            config.facets,
            config.containsLiveMap
        )

        val assembler = PlotConfigFrontendUtil.createPlotAssembler(
            config,
            sharedContinuousDomainX,
            sharedContinuousDomainY,
        )
        return PlotFigureBuildInfo(
            assembler,
            config.toMap(),
            DoubleRectangle(DoubleVector.ZERO, preferredSize),
            computationMessages
        )
    }

    private fun buildCompositeFigureFromProcessedSpecs(
        plotSpec: Map<String, Any>,
        sizingPolicy: SizingPolicy?,
    ): CompositeFigureBuildInfo {
        val computationMessages = ArrayList<String>()
        val compositeFigureConfig = CompositeFigureConfig(plotSpec, containerTheme = null) {
            computationMessages.addAll(it)
        }

        val preferredSize = PlotSizeHelper.compositeFigureSize(
            compositeFigureConfig,
            sizingPolicy,
        )

        return buildCompositeFigure(
            compositeFigureConfig,
            preferredSize,
            computationMessages
        )
    }

    private fun buildCompositeFigure(
        config: CompositeFigureConfig,
        preferredSize: DoubleVector,
        computationMessages: MutableList<String>,
    ): CompositeFigureBuildInfo {

        val compositeFigureLayout = config.layout

        val sharedXDomains: List<DoubleSpan?>?
        val sharedYDomains: List<DoubleSpan?>?
        if (compositeFigureLayout is CompositeFigureGridLayoutBase &&
            compositeFigureLayout.hasSharedAxis()
        ) {
            val sharedDomainsXY = FigureGridScaleShareUtil.getSharedDomains(
                elementConfigs = config.elementConfigs,
                gridLayout = compositeFigureLayout
            )
            sharedXDomains = sharedDomainsXY.first
            sharedYDomains = sharedDomainsXY.second
        } else {
            sharedXDomains = null
            sharedYDomains = null
        }

        val elements: List<FigureBuildInfo?> = config.elementConfigs.mapIndexed { index, element ->
            element?.let {
                when (PlotConfig.figSpecKind(it)) {
                    FigKind.PLOT_SPEC -> buildSinglePlot(
                        config = it as PlotConfigFrontend,
                        sizingPolicy = null, // Doesn't matter - will be updateed by sub-plots layout.
                        sharedContinuousDomainX = sharedXDomains?.get(index),
                        sharedContinuousDomainY = sharedYDomains?.get(index),
                        computationMessages = emptyList()  // No "own messages" when a part of a composite.
                    )

                    FigKind.SUBPLOTS_SPEC -> {
                        buildCompositeFigure(
                            config = it as CompositeFigureConfig,
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
            layout = compositeFigureLayout,
            bounds = DoubleRectangle(DoubleVector.ZERO, preferredSize),
            theme = config.theme,
            computationMessages
        )
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
     * @param plotSpec: raw specifications of a plot
     */
    fun processRawSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
        // Internal use: testing
        if (plotSpec["kind"]?.toString() == Option.Meta.Kind.ERROR_GEN) {
            return SpecTransformBackendUtil.processTransform(plotSpec, simulateFailure = true)
        }

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