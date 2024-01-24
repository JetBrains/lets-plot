/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.geom

import demo.plot.common.model.SimpleDemoBase
import demo.plot.shared.data.KansasPolygon.KANSAS_X
import demo.plot.shared.data.KansasPolygon.KANSAS_Y
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.array
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.geom.PolygonGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.builder.SvgLayerRenderer
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapper
import kotlin.math.*

open class PolygonWithCoordMapDemo : SimpleDemoBase() {

    private val bbox = arrayOfNulls<Double>(4)

    fun createModels(): List<GroupComponent> {
        return listOf(
            create()
        )
    }

    private fun toColors(values: Iterable<Double>): List<Color> {
        val mapper = colorMapper(values)
        val colors = ArrayList<Color>()
        for (value in values) {
            colors.add(mapper(value))
        }
        return colors
    }

    private fun colorMapper(values: Iterable<Double>): (Double?) -> Color {
        return ColorMapper.gradient(SeriesUtil.range(values)!!, Color.DARK_BLUE, Color.LIGHT_BLUE, Color.GRAY)
    }

    private fun create(): GroupComponent {
        val values: Array<Double> = Array(KANSAS_X.size) { 55.0 }
        val groups: Array<Int> = Array(KANSAS_X.size) { 1 }

        val coordsX = KANSAS_X.toList()
        val coordsY = KANSAS_Y.toList()
        val domainX = SeriesUtil.range(coordsX)!!
        val domainY = SeriesUtil.range(coordsY)!!
//        val spanX = domainX.upperEnd - domainX.lowerEnd
//        val spanY = domainY.upperEnd - domainY.lowerEnd
//        val clientW = demoInnerSize.x
//        val clientH = demoInnerSize.y
//        val ratioX = spanX / clientW
//        val ratioY = spanY / clientH
//        val mapper: ScaleMapper<Double>
////        val ratio: Double
//        if (ratioX >= ratioY) {
//            mapper = Mappers.mul(domainX, clientW)
////            ratio = ratioX
//        } else {
//            mapper = Mappers.mul(domainY, clientH)
////            ratio = ratioY
//        }
//        val lengthX = spanX / ratio
//        val lengthY = spanY / ratio
//        val mapperX = Mappers.mul(domainX, lengthX)
//        val mapperY = Mappers.mul(domainY, lengthY)
        val aes = AestheticsBuilder(KANSAS_X.size)
//            .x(listMapper(coordsX, mapper))
//            .y(listMapper(coordsY, mapper))
            .x(list(coordsX))
            .y(list(coordsY))
            .fill(list(toColors(values.toList())))
            .group(array(groups))
            .color(constant(Color.DARK_MAGENTA))
            .alpha(constant(0.5))
            .build()
        val coord = CoordProviders.map().let {
            val adjustedDomain = it.adjustDomain(DoubleRectangle(domainX, domainY))
            it.createCoordinateSystem(
                adjustedDomain = adjustedDomain,
                clientSize = demoInnerSize
            )
        }
        val layer = SvgLayerRenderer(
            aes,
            PolygonGeom(),
            PositionAdjustments.identity(),
            coord,
            EMPTY_GEOM_CONTEXT
        )
        val groupComponent = GroupComponent()
        groupComponent.add(layer.rootGroup)
        return groupComponent
    }

    private fun project(x: Double?, y: Double?): Array<Double> {
        val r = 6378137.0
        val maxLatitude = 85.0511287798
        val d = PI / 180
        val lat = max(min(maxLatitude, y!!), -maxLatitude)

        val rx = r * x!! * d
        val ry = ln(tan(PI / 4 + toRadians(lat) / 2)) * r

        return arrayOf(rx, ry)
    }
}
