package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.BatikMapperDemoFrame
import jetbrains.datalore.visualization.base.swing.SwingDemoFactory
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactoryBatik : SwingDemoFactory {
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