/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbraibs.letsPlot.awt.canvas

import java.awt.geom.AffineTransform
import kotlin.test.Test
import kotlin.test.assertEquals

class AffineTest {
    @Test
    fun `translate concat`() {
        val tr = AffineTransform()
        tr.setToTranslation(10.0, 10.0)

        val at = AffineTransform()
        at.concatenate(tr)

        assertEquals(at.scaleX, 1.0)
        assertEquals(at.scaleY, 1.0)
        assertEquals(at.translateX, 10.0)
        assertEquals(at.translateY, 10.0)
        assertEquals(at.shearX, 0.0)
        assertEquals(at.shearY, 0.0)
    }

    @Test
    fun `scale concat`() {
        val tr = AffineTransform()
        tr.setToScale(2.0, 2.0)

        val at = AffineTransform()
        at.concatenate(tr)

        assertEquals(at.scaleX, 2.0)
        assertEquals(at.scaleY, 2.0)
        assertEquals(at.translateX, 0.0)
        assertEquals(at.translateY, 0.0)
        assertEquals(at.shearX, 0.0)
        assertEquals(at.shearY, 0.0)
    }

    @Test
    fun `rotate concat`() {
        val tr = AffineTransform()
        tr.setToRotation(Math.PI / 2)

        val at = AffineTransform()
        at.concatenate(tr)

        assertEquals(at.scaleX, 0.0)
        assertEquals(at.scaleY, 0.0)
        assertEquals(at.translateX, 0.0)
        assertEquals(at.translateY, 0.0)
        assertEquals(at.shearX, -1.0)
        assertEquals(at.shearY, 1.0)
    }

    @Test
    fun `shear concat`() {
        val tr = AffineTransform()
        tr.setToShear(1.0, 1.0)

        val at = AffineTransform()
        at.concatenate(tr)

        assertEquals(at.scaleX, 1.0)
        assertEquals(at.scaleY, 1.0)
        assertEquals(at.translateX, 0.0)
        assertEquals(at.translateY, 0.0)
        assertEquals(at.shearX, 1.0)
        assertEquals(at.shearY, 1.0)
    }

    @Test
    fun `translate scale rotate shear concat`() {
        val tr = AffineTransform()
        tr.translate(10.0, 10.0)

        val sc = AffineTransform()
        sc.scale(2.0, 2.0)

        val rt = AffineTransform()
        rt.rotate(Math.PI / 2)

        val sh = AffineTransform()
        sh.shear(1.0, 1.0)

        val at = AffineTransform()
        at.concatenate(tr)
        at.concatenate(sc)
        at.concatenate(rt)
        at.concatenate(sh)

        assertEquals(-2.0, at.scaleX)
        assertEquals(2.0, at.scaleY)
        assertEquals(10.0, at.translateX)
        assertEquals(10.0, at.translateY)
        assertEquals(-2.0, at.shearX)
        assertEquals(2.0, at.shearY)
    }

}