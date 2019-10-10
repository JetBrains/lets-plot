package jetbrains.datalore.plot.base.render

import jetbrains.datalore.vis.svg.SvgNode

interface SvgRoot {
    fun add(node: SvgNode)
}
