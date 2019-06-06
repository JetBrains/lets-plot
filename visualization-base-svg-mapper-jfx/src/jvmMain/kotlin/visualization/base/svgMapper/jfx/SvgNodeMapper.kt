package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Node
import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.visualization.base.svg.SvgNode

internal open class SvgNodeMapper<SourceT : SvgNode, TargetT : Node>(
        source: SourceT,
        target: TargetT,
        protected val peer: SvgAwtPeer) : Mapper<SourceT, TargetT>(source, target) {

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        peer.registerMapper(source, this)
    }

    override fun onDetach() {
        super.onDetach()

        peer.unregisterMapper(source)
    }
}