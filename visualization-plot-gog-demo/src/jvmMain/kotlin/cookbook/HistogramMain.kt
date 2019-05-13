package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.Histogram

class HistogramMain : Histogram() {

    private fun show() {
        val plotSpecList = listOf(
                basic(),
                withWeights(),
                withConstantWeight()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HistogramMain().show()
        }
    }
}
