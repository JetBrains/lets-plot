package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgNode
import org.apache.batik.dom.AbstractDocument
import org.w3c.dom.Node

internal open class SvgNodeMapper<SourceT : SvgNode, TargetT : Node>(
        source: SourceT,
        target: TargetT,
        private val myDoc: AbstractDocument,
        private val myPeer: SvgAwtPeer) : Mapper<SourceT, TargetT>(source, target) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val target = Utils.elementChildren(target)
            conf.add(Synchronizers.forObservableRole(
                    this,
                    source.children(),
                    target,
                    SvgNodeMapperFactory(myDoc, myPeer)
            ))
        } else {
            conf.add(SvgNodeSubtreeGeneratingSynchronizer(source, target, myDoc))
        }
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        myPeer.registerMapper(source, this)
    }

    override fun onDetach() {
        super.onDetach()

        myPeer.unregisterMapper(source)
    }
}