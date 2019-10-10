package jetbrains.datalore.vis.svg.slim

import jetbrains.datalore.vis.svg.SvgNode

interface SvgSlimGroup : SvgSlimObject {
    //  void addChild(SvgSlimObject child);
    fun asDummySvgNode(): SvgNode
}
