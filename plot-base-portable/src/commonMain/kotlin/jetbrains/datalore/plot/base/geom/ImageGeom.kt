/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgImageElement

/**
 * Creates SvgImageElement and assign 'imageUrl' value to 'href' attribute.
 */
class ImageGeom(private val imageUrl: String) : GeomBase() {

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
            boundsClient.dimension.x, boundsClient.dimension.y
        )
        svgImageElement.href().set(imageUrl)
        root.add(svgImageElement)
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.XMIN,
//                Aes.XMAX,
//                Aes.YMIN,
//                Aes.YMAX
//        )

        const val HANDLES_GROUPS = false
    }
}

