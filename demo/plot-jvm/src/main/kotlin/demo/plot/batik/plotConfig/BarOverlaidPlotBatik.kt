package demo.plot.batik.plotConfig

import demo.common.utils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.BarOverlaidPlot

fun main() {
    with(BarOverlaidPlot()) {
        PlotSpecsDemoWindowBatik(
            "Overlaid bars plot",
            plotSpecList()
        ).open()
    }
}