package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent

interface SwingDemoFactory {
    fun createDemoFrame(title: String,
                        size: Dimension = SwingDemoFrame.FRAME_SIZE): SwingDemoFrame

    fun createSvgComponent(svg: SvgSvgElement): JComponent
}

