/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.plot.builder.PlotContainerPortable
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.BunchConfig
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import jetbrains.datalore.vis.svgToString.SvgToString


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
        val buildResult = buildPlotsFromProcessedSpecs(plotSpec, plotSize, plotMaxWidth = null)
        if (buildResult.isError) {
            val errorMessage = (buildResult as PlotsBuildResult.Error).error
            throw RuntimeException(errorMessage)
        }

        val success = buildResult as PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        if (computationMessages.isNotEmpty()) {
            computationMessagesHandler(computationMessages)
        }

        return success.buildInfos.map {

            val assembler = it.plotAssembler
            val plot = assembler.createPlot()
            val plotContainer = PlotContainerPortable(plot, it.size)

            plotContainer.ensureContentBuilt()
            plotContainer.svg
        }.map { svgToString.render(it) }
    }


    fun buildPlotsFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?
    ): PlotsBuildResult {
        throwTestingErrors()  // noop
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            return PlotsBuildResult.Error(errorMessage)
        }

        return when {
            PlotConfig.isPlotSpec(plotSpec) -> {
                PlotsBuildResult.Success(
                    listOf(
                        buildSinglePlotFromProcessedSpecs(
                            plotSpec,
                            plotSize,
                            plotMaxWidth
                        )
                    )
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
        if (bunchConfig.bunchItems.isEmpty()) return PlotsBuildResult.Error(
            "No plots in the bunch"
        )

        val buildInfos = ArrayList<PlotBuildInfo>()
        for (bunchItem in bunchConfig.bunchItems) {
            val plotSpec = bunchItem.featureSpec as MutableMap<String, Any>
            var buildInfo =
                buildSinglePlotFromProcessedSpecs(
                    plotSpec,
                    PlotSizeHelper.bunchItemSize(bunchItem),
                    plotMaxWidth = null
                )

            buildInfo = PlotBuildInfo(
                buildInfo.plotAssembler,
                buildInfo.processedPlotSpec,
                DoubleVector(bunchItem.x, bunchItem.y),  // true origin
                buildInfo.size,
                buildInfo.computationMessages
            )
            buildInfos.add(buildInfo)
        }

        return PlotsBuildResult.Success(buildInfos)
    }


    private fun buildSinglePlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?
    ): PlotBuildInfo {

        val computationMessages = ArrayList<String>()
        val config = PlotConfigClientSide.create(plotSpec) {
            computationMessages.addAll(it)
        }

        val preferredSize = ValueProperty(
            PlotSizeHelper.singlePlotSize(
                plotSpec,
                plotSize,
                plotMaxWidth,
                config.facets,
                config.containsLiveMap
            )
        )

        val assembler = createPlotAssembler(config)
        return PlotBuildInfo(
            assembler,
            plotSpec,
            DoubleVector.ZERO,
            preferredSize,
            computationMessages
        )
    }

    internal fun createPlotAssembler(
        config: PlotConfigClientSide
    ): PlotAssembler {
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
     * Applies all transformations to plot specifications.
     * @param plotSpec: raw specifications of a single plot or GGBunch
     */
    @Suppress("DuplicatedCode")
    fun processRawSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Only "portable" transforms (not supported: raster image, any async transforms)

        // Backend transforms
        @Suppress("NAME_SHADOWING")
        val plotSpec =
            if (frontendOnly) {
                plotSpec
            } else {
                PlotConfigServerSide.processTransform(plotSpec)
            }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Frontend transforms
        return PlotConfigClientSide.processTransform(plotSpec)
    }


    sealed class PlotsBuildResult {
        val isError: Boolean = this is Error

        class Error(val error: String) : PlotsBuildResult()

        class Success(
            val buildInfos: List<PlotBuildInfo>
        ) : PlotsBuildResult()
    }

    class PlotBuildInfo(
        val plotAssembler: PlotAssembler,
        val processedPlotSpec: MutableMap<String, Any>,
        val origin: DoubleVector,
        val size: ReadableProperty<DoubleVector>,     // TODO: ReadableProperty or just DoubleVector?
        val computationMessages: List<String>
    ) {
        fun bounds(): DoubleRectangle {
            return DoubleRectangle(origin, size.get())
        }
    }
}