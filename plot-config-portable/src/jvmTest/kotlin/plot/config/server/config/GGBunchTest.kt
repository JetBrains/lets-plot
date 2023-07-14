/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.SimpleTestSpecs.simpleBunch
import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import org.junit.Test
import kotlin.test.assertEquals

class GGBunchTest {
    @Test
    fun testBackendTransform() {
        val geom0 = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.PlotBase.MAPPING to mapOf(Aes.X.name to List(50) { 1.0 }),
            "bins" to 1
        )

        val geom1 = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.PlotBase.MAPPING to mapOf(Aes.X.name to List(100) { 1.0 }),
            "bins" to 1
        )

        val ggBunch = simpleBunch(listOf(geom0, geom1))

        // Transform
        BackendSpecTransformUtil.processTransform(ggBunch)

        // Expected
        val geom0Exp = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.PlotBase.MAPPING to mapOf(Aes.X.name to "x"),
            Option.PlotBase.DATA to mapOf("..count.." to listOf(50.0), "x" to listOf(1.0)),
            "bins" to 1
        )
        val geom1Exp = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.PlotBase.MAPPING to mapOf(Aes.X.name to "x"),
            Option.PlotBase.DATA to mapOf("..count.." to listOf(100.0), "x" to listOf(1.0)),
            "bins" to 1
        )
        val ggBunchExp = simpleBunch(listOf(geom0Exp, geom1Exp))

        // Test
        assertEquals(ggBunchExp, ggBunch)
    }
}