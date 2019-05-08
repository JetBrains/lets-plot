package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.LegendShowByGeom
import java.util.*

class LegendShowByGeomMain : LegendShowByGeom() {

    private fun show() {
        val plotSpecList = Arrays.asList(
                LegendShowByGeom.defaultLegend(),
                LegendShowByGeom.noLinesLegend(),
                LegendShowByGeom.noBothLegends()
        )

        SwingDemoUtil.show(viewSize, plotSpecList)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LegendShowByGeomMain().show()
        }
    }
}
