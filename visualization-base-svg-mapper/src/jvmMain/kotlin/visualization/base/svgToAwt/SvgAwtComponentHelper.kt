package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

class SvgAwtComponentHelper private constructor(
        val nodeContainer: SvgNodeContainer,
        override val messageCallback: MessageCallback) : SvgAwtHelper() {

    companion object {
        fun forUnattached(svgRoot: SvgSvgElement, messageCallback: MessageCallback): SvgAwtComponentHelper {
            Preconditions.checkArgument(!svgRoot.isAttached(), "SvgSvgElement must be unattached")
            // element must be attached
            val nodeContainer = SvgNodeContainer(svgRoot)
            val helper = SvgAwtComponentHelper(nodeContainer, messageCallback)
            helper.setSvg(svgRoot)
            return helper
        }
    }
}
