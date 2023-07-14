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
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.splitRings
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.VertexSampling.VertexDpSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.VertexSampling.VertexVwSampling
import kotlin.test.Test

class VertexSamplingTest {

    private fun toDF(points: List<DoubleVector>): DataFrame {
        val builder = Builder()
        return builder
            .put(TransformVar.X, points.map { p -> p.x })
            .put(TransformVar.Y, points.map { p -> p.y })
            .build()
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

        val p = splitRings(VertexDpSampling(4).apply(df))

        assertThat(p[0]).hasSize(4)
    }

    @Test
    fun forRing_whenLimitIs3_ShouldReturnEmptyList() {
        val df = toDF(createCircle(50000, 10.0))

        val simplifiedRings = splitRings(VertexDpSampling(3).apply(df))
        assertThat(simplifiedRings).isEmpty()
    }

    @Test
    fun forLine_whenLimitIs3_ShouldReturnEmptyList() {
        val df = toDF(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(5.0, 5.0),
                DoubleVector(10.0, 10.0)
            )
        )

        val simplifiedRings = splitRings(VertexDpSampling(2).apply(df))
        assertThat(simplifiedRings).hasSize(1)
        assertThat(simplifiedRings[0])
            .containsExactly(
                DoubleVector(0.0, 0.0),
                DoubleVector(10.0, 10.0)
            )
    }

    @Test
    fun dpSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = splitRings(VertexDpSampling(8).apply(df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Test
    fun vwSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = splitRings(VertexVwSampling(8).apply(df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Test
    fun ringsPointsDistributionCase1() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(100, 100.0))
        polygon.addAll(createCircle(1000, 50.0))
        polygon.addAll(createCircle(10000, 70.0))
        val df = toDF(polygon)

        val simplifiedRings = splitRings(VertexVwSampling(500).apply(df))
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

        val simplifiedRings = splitRings(VertexDpSampling(10000).apply(df))
        assertThat(simplifiedRings).hasSize(2)
        assertThatRing(simplifiedRings[0]).hasArea(314.159)
        assertThatRing(simplifiedRings[1]).hasArea(40000.0)
    }

    @Test
    fun ringsPointsDistributionCase3() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(10000, 100.0))
        polygon.addAll(createCircle(10000, 50.0))
        polygon.addAll(createCircle(10000, 70.0))

        val simplifiedRings = splitRings(VertexDpSampling(100).apply(toDF(polygon)))

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

        val simplifiedRings = splitRings(VertexDpSampling(30).apply(toDF(polygon)))

        assertThat(simplifiedRings).hasSize(2)
        assertThatRing(simplifiedRings[0]).isClosed.hasSize(19)
        assertThatRing(simplifiedRings[1]).isClosed.hasSize(11)
    }

    companion object {
        fun getPointsCount(rings: List<List<DoubleVector>>): Int {
            return rings.map { it.size }.sum()
        }
    }
}