package jetbrains.datalore.plot

import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.config.PlotConfigUtil
import jetbrains.datalore.plot.server.config.PlotConfigServerSide

object Monolithic {
    fun createPlot(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): Plot {

        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)

        @Suppress("NAME_SHADOWING")
        val plotSpec = transformPlotSpec(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (!computationMessages.isEmpty()) {
                computationMessagesHandler(computationMessages)
            }
        }

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(plotSpec)
        return assembler.createPlot()
    }

    private fun transformPlotSpec(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        return PlotConfigClientSide.processTransform(plotSpec)
    }
}
