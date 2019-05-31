package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent

class SwingDemoFactoryBatik : SwingDemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return SwingDemoFrameBatik(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return SwingDemoFrameBatik.createSvgComponent(svg)
    }

    override fun plotEdtExecutor(): (() -> Unit) -> Unit {
        // Just invoke in current thread
        return { runnable ->
            runnable.invoke()
        }
    }
}