package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.swing.SceneMapperDemoFrame
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel
import javax.swing.SwingUtilities

class SvgElementsDemoSceneMapper {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { show() }
        }

        private fun show() {
            val svgRoots = listOf(DemoModel.createModel())
            SceneMapperDemoFrame.showSvg(svgRoots,
                    listOf("/svgMapperDemo.css"),
                    DoubleVector(500.0, 300.0),
                    "Svg Elements (JFX SVG mapper)")
        }
    }
}