/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.geom

import jetbrains.datalore.base.spatial.GeoUtils.toRadians
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.listMapper
import jetbrains.datalore.plot.base.geom.PolygonGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plotDemo.model.KansasPolygon.KANSAS_X
import jetbrains.datalore.plotDemo.model.KansasPolygon.KANSAS_Y
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
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
        val spanX = domainX.upperEndpoint() - domainX.lowerEndpoint()
        val spanY = domainY.upperEndpoint() - domainY.lowerEndpoint()
        val clientW = demoInnerSize.x
        val clientH = demoInnerSize.y
        val ratioX = spanX / clientW
        val ratioY = spanY / clientH
        val mapper: (Double?) -> Double?
        val ratio: Double
        if (ratioX >= ratioY) {
            mapper = Mappers.mul(domainX, clientW)
            ratio = ratioX
        } else {
            mapper = Mappers.mul(domainY, clientH)
            ratio = ratioY
        }
        val lengthX = spanX / ratio
        val lengthY = spanY / ratio
        val aes = AestheticsBuilder(KANSAS_X.size)
                .x(listMapper(coordsX, mapper))
                .y(listMapper(coordsY, mapper))
                .fill(collection(toColors(values.toList())))
                .group(array(groups))
                .color(constant(Color.DARK_MAGENTA))
                .alpha(constant(0.5))
                .build()
        val coord = jetbrains.datalore.plot.builder.coord.CoordProviders.map()
                .createCoordinateSystem(domainX, lengthX, domainY, lengthY)
        val layer = jetbrains.datalore.plot.builder.SvgLayerRenderer(
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
