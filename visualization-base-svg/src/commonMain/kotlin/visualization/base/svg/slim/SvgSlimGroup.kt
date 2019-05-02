package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.visualization.base.svg.SvgNode

interface SvgSlimGroup : SvgSlimObject {
    //  void addChild(SvgSlimObject child);
    fun asDummySvgNode(): SvgNode
}
