package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Parent
import javafx.scene.layout.Pane
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

class SvgSvgElementMapper(
        source: SvgSvgElement,
        peer: SvgAwtPeer) : SvgElementMapper<SvgSvgElement, Parent>(source, createTargetContainer(), peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        val targetList = Utils.elementChildren(target)
        conf.add(Synchronizers.forObservableRole(
                this,
                source.children(),
                targetList,
                SvgNodeMapperFactory(peer)
        ))
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }
        val peer = SvgAwtPeer()
        source.container().setPeer(peer)
    }

    override fun onDetach() {
        if (source.isAttached()) {
            source.container().setPeer(null)
        }
        super.onDetach()
    }

    companion object {
        private fun createTargetContainer(): Parent {
            val pane = Pane()

            // this makes lines sharp
            // but we have to un-scale all x,y,width,height etc accordingly.
/*
            pane.scaleX = 0.5
            pane.scaleY = 0.5

            The scale factor can be determined as
            Toolkit.getDefaultToolkit().getScreenResolution() / 96.

            1 -> standard resolution (no scaling)
            2 -> retina
            ...
            see: https://josm.openstreetmap.de/ticket/9995
*/

//            pane.style = "-fx-border-color: red; -fx-border-width: 0 5; -fx-background-color: #2f4f4f"
//            pane.style = "-fx-border-width: 0"
//            pane.snapToPixelProperty().set(true)
//            pane.style = "-fx-padding: 0"

            return pane
        }
    }
}