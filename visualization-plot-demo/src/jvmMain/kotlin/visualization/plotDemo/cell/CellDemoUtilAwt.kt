package jetbrains.datalore.visualization.plotDemo.cell

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame

object CellDemoUtilAwt {
    @JvmStatic
    fun main(args: Array<String>) {
        show(DoubleVector.ZERO)
    }


    private fun show(demoComponentSize: DoubleVector) {
        SwingDemoFrame("").show {
            val widthControl = WidthControl(600)
            add(widthControl)
        }
    }
}