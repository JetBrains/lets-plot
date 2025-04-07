/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.assertj.core.api.Assertions.assertThat
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

        assertThat(tp.round()).isEqualTo(DoubleVector(0.0, -1.0))
    }

    @Test
    fun asd() {
        val t = AffineTransform.makeTransform(sx=1.0, ry=0.3420201241970062, rx=0.0, sy=1.0, tx=0.0, ty=-30.0)
        val p = DoubleVector(150.0, 375.0)

        val tp = t.transform(p.x, p.y)
        assertThat(tp.round()).isEqualTo(DoubleVector(150, 396))
    }


    fun DoubleVector.round() : DoubleVector {
        return DoubleVector(
            x = this.x.toInt().toDouble(),
            y = this.y.toInt().toDouble()
        )
    }

}

