/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option.GeomName
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase
import org.jetbrains.letsPlot.core.spec.Option.SubPlots

object SimpleTestSpecs {
    fun simplePointLayer(): MutableMap<String, Any> {
        return mutableMapOf(
            Layer.GEOM to GeomName.POINT,
            PlotBase.MAPPING to mapOf(
                Aes.X.name to List(50) { 1.0 },
                Aes.Y.name to List(50) { 1.0 }
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
        val figures = geoms.map { geom ->
            // Single-layer plot
            mutableMapOf(
                Meta.KIND to Kind.PLOT,
                PlotBase.MAPPING to emptyMap<Any, Any>(),
                Plot.LAYERS to listOf(
                    geom
                )
            )
        }

        val ggBunch = mutableMapOf<String, Any>(
            Meta.KIND to Kind.SUBPLOTS,
            SubPlots.FIGURES to figures,
            SubPlots.LAYOUT to mapOf(
                Meta.NAME to SubPlots.Layout.SUBPLOTS_FREE,
                SubPlots.Free.REGIONS to listOf(
                    listOf(0, 0, 0.5, 0.5) // 'regions' are optional
                ),
            ),
        )

        return ggBunch
    }
}