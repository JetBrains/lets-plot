package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.visualization.base.svg.SvgElement
import org.w3c.dom.svg.SVGElement

internal class SvgElementMapper<SourceT : SvgElement, TargetT : SVGElement>(source: SourceT, target: TargetT, peer: SvgDomPeer) : SvgNodeMapper<SourceT, TargetT>(source, target, peer) {

}