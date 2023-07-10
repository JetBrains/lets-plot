/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.legend.GenericLegendKeyElementFactory
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot


class LiveMapGeom : Geom {
    private lateinit var myMapProvider: LiveMapProvider

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = GenericLegendKeyElementFactory()

    override fun build(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        throw IllegalStateException("Not applicable to live map")
    }

    fun setLiveMapProvider(liveMapProvider: LiveMapProvider) {
        myMapProvider = liveMapProvider
    }

    fun createCanvasFigure(bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
        return myMapProvider.createLiveMap(bounds)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
