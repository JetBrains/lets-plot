package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BarOverlaidPlot
import jetbrains.datalore.vis.demoUtils.PlotSpecsDemoWindowBatik

fun main() {
    with(BarOverlaidPlot()) {
        PlotSpecsDemoWindowBatik(
            "Overlaid bars plot",
            plotSpecList()
        ).open()
    }
}