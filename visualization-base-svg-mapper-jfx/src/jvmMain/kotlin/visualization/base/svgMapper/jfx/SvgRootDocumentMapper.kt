package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

class SvgRootDocumentMapper(source: SvgSvgElement) : Mapper<SvgSvgElement, Group>(source, Group()) {

    private var myRootMapper: SvgSvgElementMapper? = null

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }
        val peer = SvgAwtPeer()
        source.container().setPeer(peer)

        myRootMapper = SvgSvgElementMapper(source, Group(), peer)
//        target.documentElement.setAttribute("shape-rendering", "geometricPrecision")
        myRootMapper!!.attachRoot()
    }

    override fun onDetach() {
        myRootMapper!!.detachRoot()
        myRootMapper = null

        if (source.isAttached()) {
            source.container().setPeer(null)
        }

        super.onDetach()
    }
}