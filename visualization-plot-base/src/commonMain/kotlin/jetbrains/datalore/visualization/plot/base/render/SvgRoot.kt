package jetbrains.datalore.visualization.plot.base.render

import jetbrains.datalore.visualization.base.svg.SvgNode

interface SvgRoot {
    fun add(node: SvgNode)
}
