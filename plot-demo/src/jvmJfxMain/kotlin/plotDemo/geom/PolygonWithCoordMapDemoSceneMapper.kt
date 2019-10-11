package jetbrains.datalore.plotDemo.geom

import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.geom.PolygonWithCoordMapDemo
import jetbrains.datalore.vis.demoUtils.jfx.SceneMapperDemoFrame

class PolygonWithCoordMapDemoSceneMapper : PolygonWithCoordMapDemo() {

    private fun show() {
        val demoModels = createModels()
        val svgRoots = createSvgRoots(demoModels)
        SceneMapperDemoFrame.showSvg(svgRoots, listOf(Style.JFX_PLOT_STYLESHEET), demoComponentSize, "Polygon with CoordMap")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PolygonWithCoordMapDemoSceneMapper().show()
        }
    }
}
