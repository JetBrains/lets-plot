package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BoxPlot
import java.util.*

class BoxPlotMain : BoxPlot() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                basic(),
                withVarWidth(),
                withCondColored(),
                withOutlierOverride(),
                withGrouping(),
                withGroupingAndVarWidth()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BoxPlotMain().show()
        }
    }
}
