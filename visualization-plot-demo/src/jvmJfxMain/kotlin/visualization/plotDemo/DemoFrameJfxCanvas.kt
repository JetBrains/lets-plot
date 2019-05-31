package jetbrains.datalore.visualization.plotDemo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.swing.SvgJfxCanvasPanel
import jetbrains.datalore.visualization.base.swing.SwingDemoFrame
import java.awt.Dimension
import javax.swing.JComponent

class DemoFrameJfxCanvas(title: String,
                         size: Dimension = FRAME_SIZE) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
        return DemoFrameJfxCanvas.createSvgComponent(svgRoot)
    }

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector, title: String) {
            DemoFrameJfxCanvas(title).showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
            return SvgJfxCanvasPanel(svgRoot)
        }
    }
}