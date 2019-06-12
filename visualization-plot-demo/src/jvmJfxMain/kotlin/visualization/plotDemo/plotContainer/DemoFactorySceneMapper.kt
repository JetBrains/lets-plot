package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.SceneMapperDemoFrame
import jetbrains.datalore.visualization.base.swing.SwingDemoFactory
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import jetbrains.datalore.visualization.base.swing.runOnFxThread
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactorySceneMapper : SwingDemoFactory {
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