package jetbrains.datalore.visualization.base.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svgMapper.batik.SvgAwtComponent
import jetbrains.datalore.visualization.base.svgMapper.batik.SvgAwtHelper
import java.awt.Dimension
import javax.swing.JComponent

class BatikMapperDemoFrame(title: String,
                           size: Dimension = FRAME_SIZE) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
        return Companion.createSvgComponent(svgRoot)
    }

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector, title: String) {
            BatikMapperDemoFrame(title).showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
            return object : SvgAwtComponent(svgRoot) {
                override fun createMessageCallback(): SvgAwtHelper.MessageCallback {
                    return createDefaultMessageCallback()
                }
            }
        }
    }
}