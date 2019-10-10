package jetbrains.datalore.visualization.demoUtils.jfx

import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFactory
import jetbrains.datalore.visualization.demoUtils.swing.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

class SceneMapperDemoFactory(private val stylesheetResource: String) : SwingDemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return SceneMapperDemoFrame(title, listOf(stylesheetResource), size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return SceneMapperDemoFrame.createSvgComponent(
            svg,
            listOf(stylesheetResource)
        )
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            runOnFxThread(runnable)
        }
    }
}