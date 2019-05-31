package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.DemoFrameBatik
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactoryBatik : DemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return DemoFrameBatik(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return DemoFrameBatik.createSvgComponent(svg)
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        // Just invoke in current thread
        return { runnable ->
            runnable.invoke()
        }
    }
}