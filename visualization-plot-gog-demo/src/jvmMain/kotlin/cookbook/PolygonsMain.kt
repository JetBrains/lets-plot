package jetbrains.datalore.visualization.gogDemo.cookbook


import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.Polygons
import java.util.*

class PolygonsMain : Polygons() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                basic()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PolygonsMain().show()
        }
    }
}
