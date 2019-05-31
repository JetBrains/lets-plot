package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.plotDemo.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

interface DemoFactory {
    fun createDemoFrame(title: String,
                        size: Dimension = SwingDemoFrame.FRAME_SIZE): SwingDemoFrame

    fun createSvgComponent(svg: SvgSvgElement): JComponent

    fun createPlotEdtExecutor(): (() -> Unit) -> Unit
}

