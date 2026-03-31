/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement

/**
 * Creates SvgImageElement and assign 'imageUrl' value to the 'href' attribute.
 */
class ImageGeom(
    private val imageUrl: String,
) : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {

        if (aesthetics.isEmpty) return

        val imageExtent = aesthetics.dataPointAt(0)
        val bbox = DoubleRectangle.span(
            DoubleVector(
                imageExtent.xmin() ?: throw IllegalArgumentException("XMIN not provided or filtered by x-scale"),
                imageExtent.ymin() ?: throw IllegalArgumentException("YMIN not provided or filtered by y-scale")
            ),
            DoubleVector(
                imageExtent.xmax() ?: throw IllegalArgumentException("XMAX not provided or filtered by x-scale"),
                imageExtent.ymax() ?: throw IllegalArgumentException("YMAX not provided or filtered by y-scale")
            )
        )

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

