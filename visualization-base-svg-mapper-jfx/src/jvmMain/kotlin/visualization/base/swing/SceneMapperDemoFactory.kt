package jetbrains.datalore.visualization.base.swing

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFactory
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

class SceneMapperDemoFactory : SwingDemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return SceneMapperDemoFrame(title, emptyList(), size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return SceneMapperDemoFrame.createSvgComponent(svg, emptyList())
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            runOnFxThread(runnable)
        }
    }
}