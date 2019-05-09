package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BarPlot
import java.util.*

class BarPlotMain : BarPlot() {

    private fun show() {
        val plotSpecList = Arrays.asList(
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
