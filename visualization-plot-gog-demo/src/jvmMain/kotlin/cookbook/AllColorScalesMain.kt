package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.AllColorScales

class AllColorScalesMain : AllColorScales() {

    private fun show() {
        SwingDemoUtil.show(viewSize, bundle() as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AllColorScalesMain().show()
        }
    }
}
