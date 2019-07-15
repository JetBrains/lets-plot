package jetbrains.datalore.visualization.base.svgMapper.dom

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svgMapper.SvgNodeSubtreeGeneratingSynchronizer
import jetbrains.datalore.visualization.base.svgMapper.dom.domUtil.DomUtil
import org.w3c.dom.Node

open class SvgNodeMapper<SourceT : SvgNode, TargetT : Node>(
    source: SourceT,
    target: TargetT,
    private val peer: SvgDomPeer
) : Mapper<SourceT, TargetT>(source, target) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            conf.add(
                Synchronizers.forObservableRole(
                    this, source.children(), DomUtil.nodeChildren(target),
                    SvgNodeMapperFactory(peer)
                )
            )
        } else {
            conf.add(SvgNodeSubtreeGeneratingSynchronizer(source, target, DomTargetPeer()))
        }
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        peer.registerMapper(source, this)
    }

    override fun onDetach() {
        super.onDetach()

        peer.unregisterMapper(source)
    }
}