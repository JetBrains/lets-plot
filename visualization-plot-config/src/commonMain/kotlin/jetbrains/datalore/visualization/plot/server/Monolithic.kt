package jetbrains.datalore.visualization.plot

import jetbrains.datalore.visualization.plot.builder.Plot
import jetbrains.datalore.visualization.plot.config.PlotConfigClientSide
import jetbrains.datalore.visualization.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.visualization.plot.config.PlotConfigUtil
import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide

object Monolithic {
    fun createPlot(plotSpec: MutableMap<String, Any>, computationMessagesHandler: ((List<String>) -> Unit)?): Plot {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = transformPlotSpec(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (!computationMessages.isEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(plotSpec)
        return assembler.createPlot()
    }

    fun transformPlotSpec(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        return PlotConfigClientSide.processTransform(plotSpec)
    }
}
