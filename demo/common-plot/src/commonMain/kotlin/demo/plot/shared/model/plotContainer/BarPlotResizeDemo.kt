/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.plotContainer

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.SimplePlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteractionBuilder

class BarPlotResizeDemo private constructor(
    private val sclData: SinCosLineData,
    private val xScale: Scale
) {

    fun createPlotAssembler(): PlotAssembler {
        val varX = sclData.varX
        val varY = sclData.varY
        val varCat = sclData.varCat
        val data = sclData.dataFrame

        val categories = ArrayList(data.distinctValues(varCat))
        val colors = listOf(Color.RED, Color.BLUE, Color.CYAN)
        val fillScale = Scales.DemoAndTest.pureDiscrete("C", categories)
        val fillMapper = Mappers.discrete(
            fillScale.transform as DiscreteTransform,
            colors, Color.GRAY
        )

        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to xScale,
            Aes.Y to Scales.DemoAndTest.continuousDomain("sin, cos, line", Aes.Y),
            Aes.FILL to fillScale
        )

        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
            Aes.FILL to fillMapper
        )

        val layerBuilder = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY, PosProvider.dodge())
            .groupingVar(varCat)
            .addBinding(VarBinding(varX, Aes.X))
            .addBinding(
                VarBinding(
                    varY,
                    Aes.Y
                )
            )
            .addBinding(VarBinding(varCat, Aes.FILL))
            .addConstantAes(Aes.WIDTH, 0.9)

        // Add bar plot interactions
        val geomInteraction = GeomInteractionBuilder.DemoAndTest(
            listOf(
                Aes.X,
                Aes.Y,
                Aes.FILL
            )
        )
            .xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            .build()

        val layer = layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)
            .build(data, null, scaleByAes, scaleMappersNP)


        val geomTiles = SimplePlotGeomTiles(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            containsLiveMap = false
        )
        val assembler = PlotAssembler.demoAndTest(
            geomTiles,
            DefaultTheme.minimal2()
        )

        return assembler
    }

    companion object {

        fun continuousX(): BarPlotResizeDemo {
            return BarPlotResizeDemo(
                SinCosLineData({ v -> v.toDouble() }, 6),
                Scales.DemoAndTest.continuousDomain(" ", Aes.X)
            )
        }

        fun discreteX(): BarPlotResizeDemo {
            val sclData = SinCosLineData({ v -> "Group label " + (v + 1) }, 6)
            return BarPlotResizeDemo(
                sclData,
                Scales.DemoAndTest.discreteDomain("", sclData.distinctXValues().toList())
            )
        }
    }
}
