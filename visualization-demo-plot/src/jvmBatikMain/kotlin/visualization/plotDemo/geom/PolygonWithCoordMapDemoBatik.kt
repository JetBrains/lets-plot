package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.vis.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.PolygonWithCoordMapDemo

class PolygonWithCoordMapDemoBatik : PolygonWithCoordMapDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        BatikMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Polygon with CoordMap")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PolygonWithCoordMapDemoBatik().show()
        }
    }
}
