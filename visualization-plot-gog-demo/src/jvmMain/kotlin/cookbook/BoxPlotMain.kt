package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BoxPlot
import java.util.*

class BoxPlotMain : BoxPlot() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                BoxPlot.basic(),
                BoxPlot.withVarWidth(),
                BoxPlot.withCondColored(),
                BoxPlot.withOutlierOverride(),
                BoxPlot.withGrouping(),
                BoxPlot.withGroupingAndVarWidth()
        )

        SwingDemoUtil.show(viewSize, plotSpecList)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BoxPlotMain().show()
        }
    }
}
