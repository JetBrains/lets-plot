package jetbrains.datalore.vis.swing

import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFactory
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFrame
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent

class BatikMapperDemoFactory : SwingDemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return BatikMapperDemoFrame(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return BatikMapperDemoFrame.createSvgComponent(svg)
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        // Just invoke in current thread
        return { runnable ->
            runnable.invoke()
        }
    }
}