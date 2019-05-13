package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BarPlot

class BarPlotMain : BarPlot() {

    private fun show() {
        val plotSpecList = listOf(
                basic(),
                fancy()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarPlotMain().show()
        }
    }
}
