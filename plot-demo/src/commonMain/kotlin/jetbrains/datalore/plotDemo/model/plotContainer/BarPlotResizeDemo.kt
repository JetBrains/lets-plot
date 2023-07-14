/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotContainer

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
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
        val fillScale = Scales.DemoAndTest.pureDiscrete("C", categories/*, colors, Color.GRAY*/)
        val fillMapper = Mappers.discrete(
            fillScale.transform as DiscreteTransform,
            colors, Color.GRAY
        )

        val scaleByAes = mapOf<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to xScale,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Scales.DemoAndTest.continuousDomain("sin, cos, line", org.jetbrains.letsPlot.core.plot.base.Aes.Y),
            org.jetbrains.letsPlot.core.plot.base.Aes.FILL to fillScale
        )

        val scaleMappersNP: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.FILL to fillMapper
        )

        val layerBuilder = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY, PosProvider.dodge())
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.bar())
//            .pos(PosProvider.dodge())
            .groupingVar(varCat)
            .addBinding(VarBinding(varX, org.jetbrains.letsPlot.core.plot.base.Aes.X))
            .addBinding(
                VarBinding(
                    varY,
                    org.jetbrains.letsPlot.core.plot.base.Aes.Y
                )
            )
            .addBinding(VarBinding(varCat, org.jetbrains.letsPlot.core.plot.base.Aes.FILL))
            .addConstantAes(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH, 0.9)

        // Add bar plot interactions
        val geomInteraction = GeomInteractionBuilder.DemoAndTest(
            listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL
            )
        )
            .xUnivariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            .build()

        val layer = layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)
            .build(data, scaleByAes, scaleMappersNP)


        val assembler = PlotAssembler.demoAndTest(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            DefaultTheme.minimal2()
        )

        return assembler
    }

    companion object {

        fun continuousX(): BarPlotResizeDemo {
            return BarPlotResizeDemo(
                SinCosLineData({ v -> v.toDouble() }, 6),
                Scales.DemoAndTest.continuousDomain(" ", org.jetbrains.letsPlot.core.plot.base.Aes.X)
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
