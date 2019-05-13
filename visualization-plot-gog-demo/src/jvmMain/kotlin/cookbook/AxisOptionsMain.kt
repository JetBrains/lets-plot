package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.AxisOptions

class AxisOptionsMain : AxisOptions() {

    private fun show() {
        val plotSpecList = listOf(
                defaultAxis(),
                noXTitle(),
                noYTitle(),
                noXTickLabels(),
                noYTickLabels(),
                noTickMarks(),
                noTickMarksOrLabels(),
                noTitlesOrLabels(),
                onlyLines(),
                noLinesOrTitles()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AxisOptionsMain().show()
        }
    }
}
