package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.Histogram
import java.util.*

class HistogramMain : Histogram() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                basic(),
                withWeights(),
                withConstantWeight()
        )

        SwingDemoUtil.show(viewSize, plotSpecList)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HistogramMain().show()
        }
    }
}
