package jetbrains.datalore.visualization.svgMapperDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.svgMapperDemo.model.DemoModel
import javax.swing.SwingUtilities

class SvgElementsDemoBatik {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { show() }
        }

        private fun show() {
            val svgRoots = listOf(DemoModel.createModel())
            BatikMapperDemoFrame.showSvg(svgRoots,
                    DoubleVector(500.0, 300.0),
                    "Svg Elements")
        }
    }
}