package jetbrains.datalore.visualization.plot.gog

import jetbrains.datalore.visualization.plot.gog.config.PlotConfigClientSide
import jetbrains.datalore.visualization.plot.gog.config.PlotConfigClientSideUtil
import jetbrains.datalore.visualization.plot.gog.config.PlotConfigUtil
import jetbrains.datalore.visualization.plot.gog.plot.Plot
import jetbrains.datalore.visualization.plot.gog.server.config.PlotConfigServerSide
import java.util.function.Consumer

object Monolithic {
    fun createPlot(plotSpec: MutableMap<String, Any>, computationMessagesHandler: Consumer<List<String>>?): Plot {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = transformPlotSpec(plotSpec)
        if (computationMessagesHandler != null) {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (!computationMessages.isEmpty()) {
                computationMessagesHandler.accept(computationMessages)
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
