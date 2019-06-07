package jetbrains.datalore.visualization.base.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent

class SvgMapperDemoFrame(title: String,
                         private val stylesheets: List<String>,
                         size: Dimension = FRAME_SIZE) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
        return Companion.createSvgComponent(svgRoot, stylesheets)
    }

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, stylesheets: List<String>, size: DoubleVector, title: String) {
            SvgMapperDemoFrame(title, stylesheets).showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement, stylesheets: List<String>): JComponent {
            return SvgMapperJfxPanel(svgRoot, stylesheets)
        }
    }
}