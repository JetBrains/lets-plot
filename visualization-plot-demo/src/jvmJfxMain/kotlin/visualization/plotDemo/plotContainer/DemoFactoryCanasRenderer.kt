package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.CanvasRendererDemoFrame
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import jetbrains.datalore.visualization.base.swing.runOnFxThread
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactoryCanasRenderer : DemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return CanvasRendererDemoFrame(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return CanvasRendererDemoFrame.createSvgComponent(svg)
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            runOnFxThread(runnable)
        }
    }
}