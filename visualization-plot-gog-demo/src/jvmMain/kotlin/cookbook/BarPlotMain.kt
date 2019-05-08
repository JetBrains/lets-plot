package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BarPlot
import java.util.*

class BarPlotMain : BarPlot() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                BarPlot.basic(),
                BarPlot.fancy()
        )

        SwingDemoUtil.show(viewSize, plotSpecList)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotMain().show()
        }
    }
}
