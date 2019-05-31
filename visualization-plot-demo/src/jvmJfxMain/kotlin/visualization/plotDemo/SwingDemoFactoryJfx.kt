package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.AbstractJfxPanel
import java.awt.Dimension
import javax.swing.JComponent

class SwingDemoFactoryJfx : SwingDemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return SwingDemoFrameJfx(title, size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return SwingDemoFrameJfx.createSvgComponent(svg)
    }

    override fun plotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            AbstractJfxPanel.runOnFxThread(runnable)
        }
    }
}