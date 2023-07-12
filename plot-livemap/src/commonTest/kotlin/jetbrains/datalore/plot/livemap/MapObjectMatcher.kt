/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.livemap.MapLayerKind.*
import jetbrains.gis.geoprotocol.Boundary
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class MapObjectMatcher {

    private var shape = Expectation.any<Int>()
    private var lineDash = Expectation.any<List<Double>>()
    private var index = Expectation.any<Int>()
    private var fillColor = Expectation.any<Color>()
    private var strokeColor = Expectation.any<Color>()
    private var strokeWidth = Expectation.any<Double>()
    private var radius = Expectation.any<Double>()
    private var frame = Expectation.any<String>()
    private val speed = Expectation.any<Double>()
    private val flow = Expectation.any<Double>()
    private var startAngle = Expectation.any<Double>()
    private var endAngle = Expectation.any<Double>()
    private var barRadius = Expectation.any<Vec<*>>()
    private var centerOffset = Expectation.any<Vec<*>>()
    private var point = Expectation.any<Vec<*>?>()
    private var geometry =
        Expectation.any<Boundary<*>>()
    private var locationBoundingBoxes =
        Expectation.any<List<Rect<*>>>()
    private var label = Expectation.any<String>()
    private var family = Expectation.any<String>()
    private var fontface = Expectation.any<String>()
    private var hjust = Expectation.any<Double>()
    private var vjust = Expectation.any<Double>()
    private var angle = Expectation.any<Double>()
    private var arrowSpec = Expectation.any<ArrowSpec>()
    private var animation = Expectation.any<Int>()

    fun match(mapEntity: DataPointLiveMapAesthetics) {
        when (mapEntity.myLayerKind) {
            PIE -> matchPieSector(mapEntity)
            // HEATMAP -> matchHeatmap(mapObject)
            V_LINE, H_LINE -> matchLine(mapEntity)
            PATH -> matchPath(mapEntity)
            POINT -> matchPoint(mapEntity)
            POLYGON -> matchPolygon(mapEntity)
            TEXT -> matchText(mapEntity)
            else -> throw IllegalStateException("Unknown map object layer kind: ${mapEntity.myLayerKind}" )
        }
    }

    private fun matchPieSector(pieSector: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(pieSector))
        index.assertExpectation(pieSector.index)
        fillColor.assertExpectation(pieSector.fillColor)
        strokeColor.assertExpectation(pieSector.strokeColor)
        strokeWidth.assertExpectation(pieSector.strokeWidth)
        radius.assertExpectation(pieSector.radius)
        // startAngle.assertExpectation(pieSector.startAngle)
        // endAngle.assertExpectation(pieSector.endAngle)
        point.assertExpectation(pieSector.point)
    }

    private fun matchHeatmap(heatmap: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(heatmap))
        index.assertExpectation(heatmap.index)
        // radius.assertExpectation(heatmap.getRadius())
        // frame.assertExpectation(heatmap.getFrame())
        point.assertExpectation(heatmap.point)
    }

    private fun matchLine(line: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(line))
        index.assertExpectation(line.index)
        lineDash.assertExpectation(line.lineDash)
        strokeColor.assertExpectation(line.strokeColor)
        strokeWidth.assertExpectation(line.strokeWidth)
        point.assertExpectation(line.point)
    }

    private fun matchPath(path: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(path))
        index.assertExpectation(path.index)
        lineDash.assertExpectation(path.lineDash)
        strokeColor.assertExpectation(path.strokeColor)
        strokeWidth.assertExpectation(path.strokeWidth)
        speed.assertExpectation(path.speed)
        flow.assertExpectation(path.flow)
        //geometry.assertExpectation(path.geometry!!)
        //arrowSpec.assertExpectation(path.getArrowSpec())
        animation.assertExpectation(path.animation)
    }

    private fun matchPoint(mapPoint: DataPointLiveMapAesthetics) {
        //locationBoundingBoxes.assertExpectation(calculateBBoxes(mapPoint))
        index.assertExpectation(mapPoint.index)
        shape.assertExpectation(mapPoint.shape)
        fillColor.assertExpectation(mapPoint.fillColor)
        strokeColor.assertExpectation(mapPoint.strokeColor)
        strokeWidth.assertExpectation(mapPoint.strokeWidth)
        radius.assertExpectation(mapPoint.radius)
        point.assertExpectation(mapPoint.point)
        label.assertExpectation(mapPoint.label)
        animation.assertExpectation(mapPoint.animation)
    }

    private fun matchPolygon(polygon: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(polygon))
        index.assertExpectation(polygon.index)
        lineDash.assertExpectation(polygon.lineDash)
        fillColor.assertExpectation(polygon.fillColor)
        strokeColor.assertExpectation(polygon.strokeColor)
        strokeWidth.assertExpectation(polygon.strokeWidth)
        //geometry.assertExpectation(polygon.geometry!!)
    }

    private fun matchText(text: DataPointLiveMapAesthetics) {
        // locationBoundingBoxes.assertExpectation(calculateBBoxes(text))
        index.assertExpectation(text.index)
        fillColor.assertExpectation(text.fillColor)
        strokeColor.assertExpectation(text.strokeColor)
        strokeWidth.assertExpectation(text.strokeWidth)
        radius.assertExpectation(text.size)


        text.point?.let { point.assertExpectation(it) }
        label.assertExpectation(text.label)
        family.assertExpectation(text.family)
        fontface.assertExpectation(text.fontface)
        hjust.assertExpectation(text.hjust)
        vjust.assertExpectation(text.vjust)
        angle.assertExpectation(text.angle)
    }

    fun shape(expectation: Expectation<Int>): MapObjectMatcher {
        shape = expectation
        return this
    }

    fun lineDash(expectation: Expectation<List<Double>>): MapObjectMatcher {
        lineDash = expectation
        return this
    }

    fun index(expectation: Expectation<Int>): MapObjectMatcher {
        index = expectation
        return this
    }

    fun fillColor(expectation: Expectation<Color>): MapObjectMatcher {
        fillColor = expectation
        return this
    }

    fun strokeColor(expectation: Expectation<Color>): MapObjectMatcher {
        strokeColor = expectation
        return this
    }

    fun strokeWidth(expectation: Expectation<Double>): MapObjectMatcher {
        strokeWidth = expectation
        return this
    }

    fun radius(expectation: Expectation<Double>): MapObjectMatcher {
        radius = expectation
        return this
    }

    fun frame(expectation: Expectation<String>): MapObjectMatcher {
        frame = expectation
        return this
    }

    fun startAngle(expectation: Expectation<Double>): MapObjectMatcher {
        startAngle = expectation
        return this
    }

    fun endAngle(expectation: Expectation<Double>): MapObjectMatcher {
        endAngle = expectation
        return this
    }

    fun barRadius(expectation: Expectation<Vec<*>>): MapObjectMatcher {
        barRadius = expectation
        return this
    }

    fun centerOffset(expectation: Expectation<Vec<*>>): MapObjectMatcher {
        centerOffset = expectation
        return this
    }

    fun point(expectation: Expectation<Vec<*>?>): MapObjectMatcher {
        point = expectation
        return this
    }

    fun geometry(expectation: Expectation<Boundary<*>>): MapObjectMatcher {
        geometry = expectation
        return this
    }

    fun locationBoundingBoxes(expectation: Expectation<List<Rect<*>>>): MapObjectMatcher {
        locationBoundingBoxes = expectation
        return this
    }

    fun label(expectation: Expectation<String>): MapObjectMatcher {
        label = expectation
        return this
    }

    fun family(expectation: Expectation<String>): MapObjectMatcher {
        family = expectation
        return this
    }

    fun fontface(expectation: Expectation<String>): MapObjectMatcher {
        fontface = expectation
        return this
    }

    fun hjust(expectation: Expectation<Double>): MapObjectMatcher {
        hjust = expectation
        return this
    }

    fun vjust(expectation: Expectation<Double>): MapObjectMatcher {
        vjust = expectation
        return this
    }

    fun angle(expectation: Expectation<Double>): MapObjectMatcher {
        angle = expectation
        return this
    }

    fun arrowSpec(expectation: Expectation<ArrowSpec>): MapObjectMatcher {
        arrowSpec = expectation
        return this
    }

    fun animation(expectation: Expectation<Int>): MapObjectMatcher {
        animation = expectation
        return this
    }

    class Expectation<T>  {
        private val expected: T?
        private val comparer: (T?, T?) -> Unit


        constructor(e: T?, c: (T?, T?) -> Unit) {
            expected = e
            comparer = c
        }

        constructor() {
            expected = null
            comparer = alwaysMatchingComparer()
        }

        constructor(c: (T?, T?) -> Unit) {
            expected = null
            comparer = c
        }

        internal fun assertExpectation(actual: T) {
            expected?.let {
                comparer(it, actual)
            }
        }

        companion object {
            internal fun <T> any(): Expectation<T> {
                return Expectation()
            }

            internal fun <T> equality(v: T?): Expectation<T> {
                return Expectation(
                    v,
                    { expected, actual -> assertEquals(expected, actual) })
            }

            internal fun <T> sizeEquality(v: Int): Expectation<List<T>> {
                return Expectation { _, t2 ->
                    assertEquals(
                        v,
                        t2!!.size
                    )
                }
            }

            internal fun <T> arrayEquality(v: Array<T>): Expectation<Array<T>> {
                return Expectation(
                    v,
                    { expecteds, actuals -> assertEquals(expecteds, actuals) })
            }

            internal fun mappingEquality(v: List<List<String>>): Expectation<List<List<String>>> {
                return Expectation(v, { expected, actual ->
                    if (expected == null && actual == null) return@Expectation

                    assertEquals(expected!!.size, actual!!.size)
                    var i = 0
                    val n = expected.size
                    while (i < n) {
                        assertEquals(
                            expected[i].toTypedArray(),
                            actual[i].toTypedArray()
                        )
                        ++i
                    }
                })
            }

            internal fun doubleEquality(v: Double, eps: Double): Expectation<Double> {
                return Expectation(
                    v,
                    { expected, actual ->
                        if (expected == null && actual == null) return@Expectation

                        assertDoubleEquals(
                            expected!!,
                            actual!!,
                            eps
                        )
                    })
            }

            internal fun pointEquality(v: Vec<*>, eps: Double): Expectation<Vec<*>?> {
                return Expectation(
                    v,
                    { expected, actual ->
                        assertPointEquals(
                            expected,
                            actual,
                            eps
                        )
                    })
            }

            internal fun doubleArrayEquality(v: List<Double>, eps: Double): Expectation<List<Double>> {
                return Expectation(
                    v,
                    { expected, actual ->
                        if (expected == null && actual == null) return@Expectation

                        assertDoubleArrayEquals(
                            expected!!,
                            actual!!,
                            eps
                        )
                    })
            }

            internal fun geometryEquality(v: Boundary<*>, eps: Double): Expectation<Boundary<*>> {
                return Expectation(
                    v,
                    { expected, actual ->
                        if (expected == null && actual == null) return@Expectation

                        assertMultipolygonEquals(
                            expected!!.asMultipolygon(),
                            actual!!.asMultipolygon(),
                            eps
                        )
                    })
            }
        }
    }

    companion object {

        private const val EPS = 0.0001
        fun <T> alwaysMatchingComparer(): (T, T) -> Unit = { _, _ -> }

        fun <T> any(): Expectation<T> {
            return Expectation.any()
        }

        fun eq(v: Double): Expectation<Double> {
            return Expectation.doubleEquality(
                v,
                EPS
            )
        }

        fun eq(v: Int): Expectation<Int> {
            return Expectation.equality(v)
        }

        fun eq(v: Vec<*>): Expectation<Vec<*>?> {
            return Expectation.pointEquality(
                v,
                EPS
            )
        }

        fun <T> eq(v: T?): Expectation<T> {
            return Expectation.equality(v)
        }

        fun <T> sizeEq(v: Int): Expectation<List<T>> {
            return Expectation.sizeEquality(v)
        }

        fun vectorEq(v: List<Double>): Expectation<List<Double>> {
            return Expectation.doubleArrayEquality(
                v,
                EPS
            )
        }

        fun geometryEq(v: Boundary<*>): Expectation<Boundary<*>> {
            return Expectation.geometryEquality(
                v,
                EPS
            )
        }

        private fun assertPointEquals(expected: Vec<*>?, actual: Vec<*>?, eps: Double) {
            if (expected != null && actual != null) {
                assertDoubleEquals(expected.x, actual.x, eps)
                assertDoubleEquals(expected.y, actual.y, eps)
            } else {
                assertNull(expected)
                assertNull(actual)
            }
        }

        private fun assertDoubleArrayEquals(expected: List<Double>, actual: List<Double>, eps: Double) {
            assertEquals(expected.size.toLong(), actual.size.toLong())
            for (i in expected.indices) {
                assertDoubleEquals(
                    expected[i],
                    actual[i],
                    eps
                )
            }
        }

        private fun assertRingEquals(expected: Ring<*>, actual: Ring<*>, eps: Double) {
            assertEquals(expected.size, actual.size)
            for (i in 0 until expected.size) {
                assertPointEquals(
                    expected[i],
                    actual[i],
                    eps
                )
            }
        }

        private fun assertPolygonEquals(expected: Polygon<*>, actual: Polygon<*>, eps: Double) {
            assertEquals(expected.size, actual.size)
            for (i in 0 until expected.size) {
                assertRingEquals(expected[i], actual[i], eps)
            }
        }

        private fun assertMultipolygonEquals(expected: MultiPolygon<*>, actual: MultiPolygon<*>, eps: Double) {
            assertEquals(expected.size, actual.size)
            for (i in 0 until expected.size) {
                assertPolygonEquals(
                    expected[i],
                    actual[i],
                    eps
                )
            }
        }

        private fun assertDoubleEquals(expected: Double, actual: Double, eps: Double) {
            if (actual < expected - eps || actual > expected + eps) {
                throw AssertionError("expected:<$expected +/- $eps> but was:<$actual>")
            }
        }
    }
}