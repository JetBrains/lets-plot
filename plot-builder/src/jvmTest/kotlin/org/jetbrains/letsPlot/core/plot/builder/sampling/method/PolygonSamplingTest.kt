/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Builder
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.rectToGeometry
import org.jetbrains.letsPlot.core.plot.builder.data.RingAssertion.Companion.assertThatRing
import org.jetbrains.letsPlot.core.plot.builder.data.createCircle
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.PolygonSampling.PolygonDpSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.PolygonSampling.PolygonVwSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.readPolygon
import kotlin.test.Ignore
import kotlin.test.Test

class PolygonSamplingTest {

    private fun singleGroupDpSampling(n: Int, df: DataFrame): DataFrame {
        return PolygonDpSampling(n).apply(df) { _ -> 0 }
    }

    private fun singleGroupVwSampling(n: Int, df: DataFrame): DataFrame {
        return PolygonVwSampling(n).apply(df) { _ -> 0 }
    }

    private fun toDF(points: List<DoubleVector>): DataFrame {
        val builder = Builder()
        return builder
            .put(TransformVar.X, points.map { p -> p.x })
            .put(TransformVar.Y, points.map { p -> p.y })
            .build()
    }

    @Test
    fun issue1168_dp() {
        val df = Builder()
            .put(TransformVar.X, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))
            .put(TransformVar.Y, listOf(0.0, 0.0, 0.0, 0.0, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
            .build()

        singleGroupDpSampling(5, df)
    }

    @Test
    fun issue1168_vw() {
        val df = Builder()
            .put(TransformVar.X, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))
            .put(TransformVar.Y, listOf(0.0, 0.0, 0.0, 0.0, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
            .build()

        singleGroupVwSampling(5, df)
    }

    @Test
    fun minimumPointsCount() {

        val df = toDF(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(0.0, 100.0),
                DoubleVector(100.0, 100.0),
                DoubleVector(100.0, 0.0),
                DoubleVector(0.0, 0.0)
            )
        )

        val p = readPolygon(singleGroupDpSampling(4, df))

        assertThat(p[0]).hasSize(4)
    }

    @Ignore("semantic was changed. revisit assertion")
    @Test
    fun forRing_whenLimitIs3_ShouldReturnEmptyList() {
        val df = toDF(createCircle(50000, 10.0))

        val simplifiedRings = readPolygon(singleGroupDpSampling(3, df))
        assertThat(simplifiedRings).isEmpty()
    }

    @Ignore("semantic was changed. revisit assertion")
    @Test
    fun forLine_whenLimitIs3_ShouldReturnEmptyList() {
        val df = toDF(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(5.0, 5.0),
                DoubleVector(10.0, 10.0)
            )
        )

        val simplifiedRings = readPolygon(singleGroupDpSampling(2, df))
        assertThat(simplifiedRings).hasSize(1)
        assertThat(simplifiedRings[0])
            .containsExactly(
                IndexedValue(0, DoubleVector(0.0, 0.0)),
                IndexedValue(1, DoubleVector(10.0, 10.0)),
            )
    }

    @Test
    fun dpSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = readPolygon(singleGroupDpSampling(8, df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Test
    fun vwSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = readPolygon(singleGroupVwSampling(8, df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Ignore("semantic was changed. revisit assertion")
    @Test
    fun ringsPointsDistributionCase1() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(100, 100.0))
        polygon.addAll(createCircle(1000, 50.0))
        polygon.addAll(createCircle(10000, 70.0))
        val df = toDF(polygon)

        val simplifiedRings = readPolygon(singleGroupVwSampling(500, df))
        assertThat(simplifiedRings).hasSize(3)
        assertThatRing(simplifiedRings[0]).hasSize(101).isClosed
        assertThatRing(simplifiedRings[1]).hasSize(135).isClosed
        assertThatRing(simplifiedRings[2]).hasSize(264).isClosed

        assertThat(
            getPointsCount(
                simplifiedRings
            )
        ).isEqualTo(500)
    }

    @Test
    fun ringsPointsDistributionCase2() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(50000, 10.0))
        polygon.addAll(rectToGeometry(0.0, 0.0, 200.0, 200.0))
        val df = toDF(polygon)

        val simplifiedRings = readPolygon(singleGroupDpSampling(10000, df))
        assertThat(simplifiedRings).hasSize(2)
        assertThatRing(simplifiedRings[0]).hasArea(314.159)
        assertThatRing(simplifiedRings[1]).hasArea(40000.0)
    }

    @Ignore("semantic was changed. revisit assertion")
    @Test
    fun ringsPointsDistributionCase3() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(10000, 100.0))
        polygon.addAll(createCircle(10000, 50.0))
        polygon.addAll(createCircle(10000, 70.0))

        val simplifiedRings = readPolygon(singleGroupDpSampling(100, toDF(polygon)))

        assertThat(simplifiedRings).hasSize(3)
        assertThatRing(simplifiedRings[0]).hasSize(57)
        assertThatRing(simplifiedRings[1]).hasSize(15)
        assertThatRing(simplifiedRings[2]).hasSize(28)

        assertThat(
            getPointsCount(
                simplifiedRings
            )
        ).isEqualTo(100)
    }

    @Ignore("semantic was changed. revisit assertion")
    @Test
    fun filteredOutRingInBetween_ShouldNotBreakRings() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(30, 200.0))
        polygon.addAll(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(0.0, 1.0),
                DoubleVector(1.0, 1.0),
                DoubleVector(0.0, 0.0)
            )
        )
        polygon.addAll(createCircle(30, 150.0))

        val simplifiedRings = readPolygon(singleGroupDpSampling(30, toDF(polygon)))

        assertThat(simplifiedRings).hasSize(2)
        assertThatRing(simplifiedRings[0]).isClosed.hasSize(19)
        assertThatRing(simplifiedRings[1]).isClosed.hasSize(11)
    }

    companion object {
        fun getPointsCount(rings: List<List<IndexedValue<DoubleVector>>>): Int {
            return rings.sumOf(List<IndexedValue<DoubleVector>>::size)
        }
    }
}