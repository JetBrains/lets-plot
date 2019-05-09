package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.LegendShowByGeom
import java.util.*

class LegendShowByGeomMain : LegendShowByGeom() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                defaultLegend(),
                noLinesLegend(),
                noBothLegends()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendShowByGeomMain().show()
        }
    }
}
