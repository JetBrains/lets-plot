package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import jetbrains.datalore.visualization.base.swing.runOnFxThread
import jetbrains.datalore.visualization.plotDemo.DemoFrameJfxCanvas
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactoryJfx : DemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return DemoFrameJfxCanvas(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return DemoFrameJfxCanvas.createSvgComponent(svg)
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            runOnFxThread(runnable)
        }
    }
}