package jetbrains.datalore.visualization.plot.gog.core.render

import jetbrains.datalore.visualization.base.svg.SvgNode

interface SvgRoot {
    fun add(node: SvgNode)
}
