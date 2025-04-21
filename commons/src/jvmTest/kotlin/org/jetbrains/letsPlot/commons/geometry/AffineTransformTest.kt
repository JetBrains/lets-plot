/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.RecursiveComparisonAssert
import org.assertj.core.util.DoubleComparator
import kotlin.test.Test

class AffineTransformTest {

    @Test
    fun `identity should not change point`() {
        val transform = AffineTransform.IDENTITY
        val point = DoubleVector(1.0, 1.0)

        val tp = transform.transform(point.x, point.y)

        assertThat(tp.round()).isEqualTo(point)
    }

    @Test
    fun `scale should scale point`() {
        val transform = AffineTransform.makeScale(2.0, 3.0)
        val p = DoubleVector(1.0, 1.0)

        val tp = transform.transform(p.x, p.y)

        assertThat(tp.round()).isEqualTo(DoubleVector(2.0, 3.0))
    }

    @Test
    fun `translate should translate point`() {
        val transform = AffineTransform.makeTranslation(2.0, 3.0)
        val p = DoubleVector(1.0, 1.0)

        val tp = transform.transform(p.x, p.y)

        assertThat(tp.round()).isEqualTo(DoubleVector(3.0, 4.0))
    }

    @Test
    fun `rotate should rotate point`() {
        val transform = AffineTransform.makeRotation(Math.PI / 2)
        val p = DoubleVector(1.0, 0.0)

        val tp = transform.transform(p.x, p.y)

        assertThat(tp.round()).isEqualTo(DoubleVector(0.0, 1.0))
    }

    @Test
    fun asd() {
        val t =
            AffineTransform.makeTransform(sx = 1.0, ry = 0.3420201241970062, rx = 0.0, sy = 1.0, tx = 0.0, ty = -30.0)
        val p = DoubleVector(150.0, 375.0)

        val tp = t.transform(p.x, p.y)
        assertThat(tp.round()).isEqualTo(DoubleVector(150, 396))
    }

    @Test
    fun `translate concat`() {
        val tr = AffineTransform.makeTranslation(10.0, 10.0)
        val at = AffineTransform.IDENTITY.concat(tr)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 1.0,
                sy = 1.0,
                tx = 10.0,
                ty = 10.0,
                ry = 0.0,
                rx = 0.0
            )
        )
    }

    @Test
    fun `scale concat`() {
        val tr = AffineTransform.makeScale(2.0, 2.0)
        val at = AffineTransform.IDENTITY.concat(tr)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 2.0,
                sy = 2.0,
                tx = 0.0,
                ty = 0.0,
                ry = 0.0,
                rx = 0.0
            )
        )
    }

    @Test
    fun `rotate concat`() {
        val tr = AffineTransform.makeRotation(Math.PI / 2)
        val at = AffineTransform.IDENTITY.concat(tr)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 0,
                ry = 1.0,
                rx = -1.0,
                sy = 0.0,
            )
        )
    }

    @Test
    fun `shear concat`() {
        val tr = AffineTransform.makeShear(1.0, 1.0)
        val at = AffineTransform.IDENTITY.concat(tr)

        assertThat(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 1.0,
                sy = 1.0,
                tx = 0.0,
                ty = 0.0,
                ry = 1.0,
                rx = 1.0
            )
        )
    }

    @Test
    fun `translate scale concat`() {
        val tr = AffineTransform.makeTranslation(10.0, 10.0)
        val sc = AffineTransform.makeScale(2.0, 2.0)

        val at = AffineTransform.IDENTITY.concat(tr).concat(sc)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 2.0,
                sy = 2.0,
                tx = 10.0,
                ty = 10.0,
                ry = 0.0,
                rx = 0.0
            )
        )
    }

    @Test
    fun `scale translate concat`() {
        val tr = AffineTransform.makeTranslation(10.0, 10.0)
        val sc = AffineTransform.makeScale(2.0, 2.0)

        val at = AffineTransform.IDENTITY.concat(sc).concat(tr)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = 2.0,
                sy = 2.0,
                tx = 20.0,
                ty = 20.0,
                ry = 0.0,
                rx = 0.0
            )
        )
    }

    @Test
    fun `translate scale rotate shear concat`() {
        val tr = AffineTransform.makeTranslation(10.0, 10.0)
        val sc = AffineTransform.makeScale(2.0, 2.0)
        val rt = AffineTransform.makeRotation(Math.PI / 2)
        val sh = AffineTransform.makeShear(1.0, 1.0)

        val at = AffineTransform.IDENTITY.concat(tr).concat(sc).concat(rt).concat(sh)

        assertTransform(at).isEqualTo(
            AffineTransform.makeTransform(
                sx = -2.0,
                sy = 2.0,
                tx = 10.0,
                ty = 10.0,
                ry = 2.0,
                rx = -2.0
            )
        )
    }


    private fun DoubleVector.round(): DoubleVector {
        return DoubleVector(
            x = this.x.toInt().toDouble(),
            y = this.y.toInt().toDouble()
        )
    }

    private fun assertTransform(actual: AffineTransform): RecursiveComparisonAssert<*> {
        return assertThat(actual).usingRecursiveComparison()
            .withComparatorForFields(DoubleComparator(0.0001))
    }

}

