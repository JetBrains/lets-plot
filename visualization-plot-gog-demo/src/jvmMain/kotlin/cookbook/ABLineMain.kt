package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.ABLine

class ABLineMain : ABLine() {

    private fun show() {
        val plotSpecList = listOf(
                lineDefaultAlone(),
                lineDefault(),
                negativeSlope(),
                zeroSlope(),
                variableInterceptAndSlope()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ABLineMain().show()
        }
    }
}
