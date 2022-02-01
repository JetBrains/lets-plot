/*
 * Copyright (c) 2022. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.stat.Stats
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformLog10DataTest {

    @Test
    fun test() {
        val n = 20
        val geomLayer = TestUtil.buildGeomLayer(
            geom = Option.GeomName.HISTOGRAM,
            data = mapOf("x" to List(n) { 0.0 }),
            mapping = mapOf(Aes.X.name to "x"),
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.Y.name,
                    Option.Scale.CONTINUOUS_TRANSFORM to Option.TransformName.LOG10
                )
            )
        )
        val dataSeries = geomLayer.dataFrame[Stats.COUNT]
        assertEquals(n.toDouble(), dataSeries.first())
    }
}