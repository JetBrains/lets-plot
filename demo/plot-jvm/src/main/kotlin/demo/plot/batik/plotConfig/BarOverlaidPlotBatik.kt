package demo.plot.batik.plotConfig

import demo.common.util.demoUtils.batik.PlotSpecsDemoWindowBatik
import demo.plot.common.model.plotConfig.BarOverlaidPlot

fun main() {
    with(BarOverlaidPlot()) {
        PlotSpecsDemoWindowBatik(
            "Overlaid bars plot",
            plotSpecList()
        ).open()
    }
}