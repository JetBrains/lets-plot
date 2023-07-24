package jetbrains.datalore.plotDemo.plotConfig

import demo.plot.common.model.plotConfig.BarOverlaidPlot
import demo.common.batik.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(BarOverlaidPlot()) {
        PlotSpecsDemoWindowBatik(
            "Overlaid bars plot",
            plotSpecList()
        ).open()
    }
}