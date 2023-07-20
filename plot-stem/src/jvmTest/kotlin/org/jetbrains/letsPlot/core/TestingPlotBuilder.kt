/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core

import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode

internal object TestingPlotBuilder {

    fun createPlot(plotSpec: MutableMap<String, Any>, andBuildComponent: Boolean = true): PlotSvgComponent {
        val plot = createPlot(plotSpec) {
            for (s in it) {
                println("PLOT MESSAGE: $s")
            }
        }
        if (andBuildComponent) {
            plot.ensureBuilt()
        }

        plotBuildErrorMessage(plot)?.let {
            throw RuntimeException(it)
        }

        return plot
    }

    private fun plotBuildErrorMessage(plotSvgComponent: PlotSvgComponent): String? {
        fun flatChildren(node: SvgNode): List<SvgNode> {
            return node.children() + node.children().flatMap(::flatChildren)
        }

        val (errorMessage, errorDescription) = flatChildren(plotSvgComponent.rootGroup)
            .mapNotNull { it as? SvgTextNode }
            .map { it.textContent().get() }
            .partition { it.contains("Error building plot") }

        if (errorMessage.isEmpty()) {
            return null
        }

        return errorDescription.joinToString()
    }

    private fun createPlot(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): PlotSvgComponent {

        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)

        @Suppress("NAME_SHADOWING")
        val plotSpec = transformPlotSpec(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            throw IllegalArgumentException(errorMessage)
        }

        val config = PlotConfigFrontend.create(plotSpec) { messages ->
            if (computationMessagesHandler != null && messages.isNotEmpty()) {
                computationMessagesHandler(messages)
            }
        }

        val assembler = PlotConfigFrontendUtil.createPlotAssembler(config)
        val layoutInfo = assembler.layoutByOuterSize(Defaults.DEF_PLOT_SIZE)
        return assembler.createPlot(layoutInfo)
    }

    private fun transformPlotSpec(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = SpecTransformBackendUtil.processTransform(plotSpec)
        return PlotConfigFrontend.processTransform(plotSpec)
    }
}
