package jetbrains.datalore.vis.svgMapper.dom

import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.vis.svg.SvgTextNode
import org.w3c.dom.Node

class SvgTextNodeMapper(source: SvgTextNode, target: Node, peer: SvgDomPeer) :
        SvgNodeMapper<SvgTextNode, Node>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(Synchronizers.forPropsOneWay(source.textContent(), object : WritableProperty<String?> {
            override fun set(value: String?) {
                target.nodeValue = value
            }
        }))
    }
}