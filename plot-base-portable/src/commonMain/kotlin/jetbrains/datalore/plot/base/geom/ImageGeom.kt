/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement

/**
 * Creates SvgImageElement and assign 'imageUrl' value to 'href' attribute.
 */
class ImageGeom(
    private val imageUrl: String,
    private val bbox: DoubleRectangle
) : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val boundsClient = coord.toClient(bbox)
        if (boundsClient == null) return

        val svgImageElement = SvgImageElement(
            boundsClient.origin.x, boundsClient.origin.y,
            boundsClient.dimension.x, boundsClient.dimension.y
        )
        svgImageElement.href().set(imageUrl)
        root.add(svgImageElement)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}

