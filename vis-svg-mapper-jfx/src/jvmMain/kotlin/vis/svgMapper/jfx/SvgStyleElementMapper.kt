package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.vis.svg.SvgStyleElement

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgJfxPeer) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        // just empty group - no synchronization.
        // CSS is added to JavaFX scene from external resource
    }
}