package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgImageElement
import jetbrains.datalore.visualization.plot.gog.core.render.*
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomHelper

internal class ImageGeom
/**
 * Creates SvgImageElement and assign 'imageUrl' value to 'href' attribute.
 *
 * @param imageUrl
 */
(private val myImageUrl: String) : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        if (aesthetics.isEmpty) return
        val p = aesthetics.dataPointAt(0)
        val boundsAes = DoubleRectangle.span(
                DoubleVector(p.xmin()!!, p.ymin()!!),
                DoubleVector(p.xmax()!!, p.ymax()!!))

        // translate to client coordinates
        val helper = GeomHelper(pos, coord, ctx)
        val boundsClient = helper.toClient(boundsAes, p)

        val svgImageElement = SvgImageElement(
                boundsClient.origin.x, boundsClient.origin.y,
                boundsClient.dimension.x, boundsClient.dimension.y)
        svgImageElement.href().set(myImageUrl)
        root.add(svgImageElement)
    }

    companion object {
        val RENDERS = listOf(
                Aes.XMIN,
                Aes.XMAX,
                Aes.YMIN,
                Aes.YMAX
        )

        val HANDLES_GROUPS = false
    }
}

