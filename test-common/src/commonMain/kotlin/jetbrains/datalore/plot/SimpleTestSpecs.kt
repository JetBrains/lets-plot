/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Meta.Kind
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase

object SimpleTestSpecs {
    fun simplePointLayer(): MutableMap<String, Any> {
        return mutableMapOf(
            Layer.GEOM to GeomName.POINT,
            PlotBase.MAPPING to mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to List(50) { 1.0 },
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to List(50) { 1.0 }
            )
        )
    }

    fun simplePlot(geoms: List<Map<String, Any?>>?): MutableMap<String, Any> {
        val specs: MutableMap<String, Any> = mutableMapOf(
            Meta.KIND to Kind.PLOT,
            PlotBase.MAPPING to emptyMap<Any, Any>()
        )

        if (geoms != null) {
            specs[Plot.LAYERS] = geoms
        }
        return specs
    }

    fun simpleBunch(geoms: List<Map<String, Any?>>): MutableMap<String, Any> {
        val itemsList = ArrayList<Map<String, Any>>()
        val ggBunch = mutableMapOf<String, Any>(
            Meta.KIND to Kind.GG_BUNCH,
            Option.GGBunch.ITEMS to itemsList
        )

        for (geom in geoms) {
            // Single-layer plot
            val plotSpec =
                mutableMapOf(
                    Meta.KIND to Kind.PLOT,
                    PlotBase.MAPPING to emptyMap<Any, Any>(),
                    Plot.LAYERS to listOf(
                        geom
                    )
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