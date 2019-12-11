/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import org.junit.Test
import kotlin.test.assertEquals

class GGBunchTest {
    @Test
    fun testBackendTransform() {
        val geom0 = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.Layer.MAPPING to mapOf(Aes.X.name to List(50) { 1.0 }),
            "bins" to 1
        )

        val geom1 = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.Layer.MAPPING to mapOf(Aes.X.name to List(100) { 1.0 }),
            "bins" to 1
        )

        val ggBunch = createSimpleBunch(listOf(geom0, geom1))

        // Transform
        PlotConfigServerSide.processTransform(ggBunch)

        // Expected
        val geom0Exp = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.Layer.MAPPING to mapOf(Aes.X.name to "x"),
            Option.Layer.DATA to mapOf("..count.." to listOf(50.0), "x" to listOf(1.0)),
            "bins" to 1
        )
        val geom1Exp = mapOf(
            Option.Layer.GEOM to Option.GeomName.HISTOGRAM,
            Option.Layer.MAPPING to mapOf(Aes.X.name to "x"),
            Option.Layer.DATA to mapOf("..count.." to listOf(100.0), "x" to listOf(1.0)),
            "bins" to 1
        )
        val ggBunchExp = createSimpleBunch(listOf(geom0Exp, geom1Exp))

        // Test
        assertEquals(ggBunchExp, ggBunch)
    }

    private fun createSimpleBunch(geoms: List<Map<String, Any?>>): MutableMap<String, Any> {
        val itemsList = ArrayList<Map<String, Any>>()
        val ggBunch = mutableMapOf<String, Any>(
            Option.Meta.KIND to Option.Meta.Kind.GG_BUNCH,
            Option.GGBunch.ITEMS to itemsList
        )

        for (geom in geoms) {
            // Single-layer plot
            val plotSpec =
                mutableMapOf(
                    Option.Meta.KIND to Option.Meta.Kind.PLOT,
                    Option.Plot.LAYERS to listOf(geom)
                )

            itemsList.add(
                mapOf(
                    Option.GGBunch.Item.X to 0.0,
                    Option.GGBunch.Item.Y to 0.0,
                    Option.GGBunch.Item.FEATURE_SPEC to plotSpec
                )
            )
        }

        return ggBunch
    }
}