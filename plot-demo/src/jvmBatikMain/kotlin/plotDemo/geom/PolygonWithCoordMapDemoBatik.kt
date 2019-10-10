package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plotDemo.model.geom.PolygonWithCoordMapDemo
import jetbrains.datalore.vis.swing.BatikMapperDemoFrame

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
