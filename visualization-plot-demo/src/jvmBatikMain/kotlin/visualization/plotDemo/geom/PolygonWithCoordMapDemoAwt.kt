package jetbrains.datalore.visualization.plotDemo.geom

import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.plotDemo.model.geom.PolygonWithCoordMapDemo

class PolygonWithCoordMapDemoAwt : PolygonWithCoordMapDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SvgMapperDemoFrame.showSvg(svgRoots, demoComponentSize, "Polygon with CoordMap")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PolygonWithCoordMapDemoAwt().show()
        }
    }
}
