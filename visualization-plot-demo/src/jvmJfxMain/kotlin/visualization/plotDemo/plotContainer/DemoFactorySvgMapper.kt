package jetbrains.datalore.visualization.plotDemo.plotContainer

import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.SvgMapperDemoFrame
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import jetbrains.datalore.visualization.base.swing.runOnFxThread
import java.awt.Dimension
import javax.swing.JComponent

class DemoFactorySvgMapper : DemoFactory {
    override fun createDemoFrame(title: String, size: Dimension): SwingDemoFrame {
        return SvgMapperDemoFrame(title, emptyList(), size)
    }

    override fun createSvgComponent(svg: SvgSvgElement): JComponent {
        return SvgMapperDemoFrame.createSvgComponent(svg, emptyList())
    }

    override fun createPlotEdtExecutor(): (() -> Unit) -> Unit {
        return { runnable ->
            runOnFxThread(runnable)
        }
    }
}