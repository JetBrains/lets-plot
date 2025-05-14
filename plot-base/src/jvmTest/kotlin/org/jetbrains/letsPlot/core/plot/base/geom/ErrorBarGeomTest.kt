/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.ThrowingConsumer
import org.assertj.core.data.Offset
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.projections.identity
import org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.BogusContext
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import kotlin.test.Test

class ErrorBarGeomTest {
    @Test
    fun `horizontal error bar with coord_map`() {
        /* plot spec
        {
            "data": {
                "xmin": [ 50.2, 51.6],
                "xmax": [ 51.5, 53.0 ],
                "y": [ 10.0, 20.0 ]
            },
            "data_meta": {
                "series_annotations": [
                    { "type": "float", "column": "xmin" },
                    { "type": "float", "column": "xmax" },
                    { "type": "int", "column": "y" }
                ]
            },
            "coord": { "name": "map", "flip": false },
            "kind": "plot",
            "layers": [
                {
                    "geom": "errorbar",
                    "mapping": { "y": "y", "xmin": "xmin", "xmax": "xmax" },
                    "width": 0.1,
                    "size": 2.0
                }
            ]
        }
        */

        val doc = DummyRoot()

        // Aesthetics flipped because of y-orientation
        ErrorBarGeom().build(
            root = doc,
            aesthetics = AestheticsBuilder(2)
                .x(list(listOf(10.0, 20.0)))
                .ymin(list(listOf(50.2, 51.6)))
                .ymax(list(listOf(51.5, 53.0)))
                .width(list(listOf(0.1, 0.1)))
                .build(),
            coord = Coords.create(
                CoordinatesMapper(
                    hScaleMapper = Mappers.mul(3.5850874352794207E-4),
                    hScaleInverseMapper = Mappers.mul(2789.3322493599385),
                    vScaleMapper = Mappers.mul(3.5850874352794207E-4),
                    vScaleInverseMapper = Mappers.mul(2789.332249359939),
                    clientBounds = DoubleRectangle.XYWH(
                        1997.845079369809,
                        358.64716244210854,
                        122.91975318535748,
                        501.0
                    ),
                    projection = mercator(),
                    flipAxis = true,
                )
            ),
            ctx = object : GeomContext by BogusContext {
                override val flipped: Boolean = false
                override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
                override fun getResolution(aes: Aes<Double>): Double = 10.0
                override fun isMappedAes(aes: Aes<*>) = aes == Aes.X || aes == Aes.YMIN || aes == Aes.YMAX
            },
            pos = PositionAdjustments.identity(),
        )

        @Suppress("UNCHECKED_CAST")
        ((doc.content[0] as SvgGElement).children() as List<SvgLineElement>).let { (leftCap, rightCap, bar) ->
            assertThat(leftCap).hasCoordinates(x1 = 5, x2 = 5, y1 = 478, y2 = 438)
            assertThat(rightCap).hasCoordinates(x1 = 57, x2 = 57, y1 = 478, y2 = 438)
            assertThat(bar).hasCoordinates(x1 = 5, x2 = 57, y1 = 458, y2 = 458)
        }

        @Suppress("UNCHECKED_CAST")
        ((doc.content[1] as SvgGElement).children() as List<SvgLineElement>).let { (leftCap, rightCap, bar) ->
            assertThat(leftCap).hasCoordinates(x1 = 61, x2 = 61, y1 = 65, y2 = 23)
            assertThat(rightCap).hasCoordinates(x1 = 117, x2 = 117, y1 = 65, y2 = 23)
            assertThat(bar).hasCoordinates(x1 = 61, x2 = 117, y1 = 44, y2 = 44)
        }
    }

    @Test
    fun `horizontal error bar with cartesian coord system`() {
        /* plot spec
        {
            "data": {
                "xmin": [ 50.2, 51.6],
                "xmax": [ 51.5, 53.0 ],
                "y": [ 10.0, 20.0 ]
            },
            "data_meta": {
                "series_annotations": [
                    { "type": "float", "column": "xmin" },
                    { "type": "float", "column": "xmax" },
                    { "type": "int", "column": "y" }
                ]
            },
            "kind": "plot",
            "layers": [
                {
                    "geom": "errorbar",
                    "mapping": { "y": "y", "xmin": "xmin", "xmax": "xmax" },
                    "width": 0.1,
                    "size": 2.0
                }
            ]
        }
         */

        val doc = DummyRoot()

        // Aesthetics flipped because of y-orientation
        ErrorBarGeom().build(
            root = doc,
            aesthetics = AestheticsBuilder(2)
                .x(list(listOf(10.0, 20.0)))
                .ymin(list(listOf(50.2, 51.6)))
                .ymax(list(listOf(51.5, 53.0)))
                .width(list(listOf(0.1, 0.1)))
                .build(),
            coord = Coords.create(
                CoordinatesMapper(
                    hScaleMapper = Mappers.mul(296.11648996398185),
                    hScaleInverseMapper = Mappers.mul(0.003377049350144719),
                    vScaleMapper = Mappers.mul(41.40495867768595),
                    vScaleInverseMapper = Mappers.mul(0.02415169660678643),
                    clientBounds = DoubleRectangle.XYWH(
                        14823.591487596932,
                        370.5743801652892,
                        912.0387890890636,
                        501.0
                    ),
                    projection = identity(),
                    flipAxis = true,
                )
            ),
            ctx = object : GeomContext by BogusContext {
                override val flipped: Boolean = false
                override val targetCollector: GeomTargetCollector = NullGeomTargetCollector()
                override fun getResolution(aes: Aes<Double>): Double = 10.0
                override fun isMappedAes(aes: Aes<*>) = aes == Aes.X || aes == Aes.YMIN || aes == Aes.YMAX
            },
            pos = PositionAdjustments.identity(),
        )

        @Suppress("UNCHECKED_CAST")
        ((doc.content[0] as SvgGElement).children() as List<SvgLineElement>).let { (leftCap, rightCap, bar) ->
            assertThat(leftCap).hasCoordinates(x1 = 41, y1 = 478, x2 = 41, y2 = 436)
            assertThat(rightCap).hasCoordinates(x1 = 426, y1 = 478, x2 = 426, y2 = 436)
            assertThat(bar).hasCoordinates(x1 = 41, y1 = 457, x2 = 426, y2 = 457)
        }

        @Suppress("UNCHECKED_CAST")
        ((doc.content[1] as SvgGElement).children() as List<SvgLineElement>).let { (leftCap, rightCap, bar) ->
            assertThat(leftCap).hasCoordinates(x1 = 456, y1 = 64, x2 = 456, y2 = 22)
            assertThat(rightCap).hasCoordinates(x1 = 870, y1 = 64, x2 = 870, y2 = 22)
            assertThat(bar).hasCoordinates(x1 = 456, y1 = 43, x2 = 870, y2 = 43)
        }
    }

    fun <T : ObjectAssert<SvgLineElement>> T.hasCoordinates(
        x1: Number,
        y1: Number,
        x2: Number,
        y2: Number,
        epsilon: Double = 1.0,
    ): ObjectAssert<SvgLineElement> {
        val precision = Offset.offset(epsilon)
        satisfies(ThrowingConsumer { element: SvgLineElement ->
            assertThat(element.x1().get()).isEqualTo(x1.toDouble(), precision)
            assertThat(element.y1().get()).isEqualTo(y1.toDouble(), precision)
            assertThat(element.x2().get()).isEqualTo(x2.toDouble(), precision)
            assertThat(element.y2().get()).isEqualTo(y2.toDouble(), precision)
        })
        return this
    }
}

