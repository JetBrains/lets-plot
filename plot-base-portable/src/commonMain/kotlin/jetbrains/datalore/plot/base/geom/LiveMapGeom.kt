/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.legend.GenericLegendKeyElementFactory
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot


class LiveMapGeom(private val myDisplayMode: DisplayMode) : Geom {
    private lateinit var myMapProvider: LiveMapProvider

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() {
            return when (myDisplayMode) {
                DisplayMode.POINT -> PointLegendKeyElementFactory()
                DisplayMode.PIE -> FilledCircleLegendKeyElementFactory()
                else -> GenericLegendKeyElementFactory()
            }
        }

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

        // ToDo: not static, depends on 'display mode'
//        val RENDERS = listOf(
//                Aes.MAP_ID,
//                Aes.ALPHA,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.SIZE,
//                Aes.SHAPE,
//                Aes.FRAME,
//                Aes.X,
//                Aes.Y
//        )
        const val HANDLES_GROUPS = false
    }
}
