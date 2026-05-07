/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.plotContainer

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.DefaultTooltipBehaviorFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.GeomInteractionUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomLayerBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.SimplePlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme

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
        val barWidth = 0.9
        val constantsMap = mapOf<Aes<*>, Any>(Aes.WIDTH to barWidth)
        val xBinding = VarBinding(varX, Aes.X)
        val yBinding = VarBinding(varY, Aes.Y)
        val fillBinding = VarBinding(varCat, Aes.FILL)
        val varBindings = listOf(
            xBinding,
            yBinding,
            fillBinding
        )

        val layerBuilder = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY, PosProvider.dodge())
            .groupingVarNames(listOf(varCat.name))
            .addBinding(xBinding)
            .addBinding(yBinding)
            .addBinding(fillBinding)
            .addConstantAes(Aes.WIDTH, barWidth)

        // Add bar plot interactions
        val tooltipBehavior = DefaultTooltipBehaviorFactory.create(
            geomKind = GeomKind.BAR,
            statKind = StatKind.IDENTITY,
            tooltipBehavior = TooltipBehavior.DEFAULT
        )
        val geomInteraction = GeomInteractionUtil.createGeomInteractionBuilder(
            bindings = varBindings.associate { it.aes to it.variable },
            scaleMap = scaleByAes,
            isLiveMap = false,
            isPolarCoordSystem = false,
            theme = DefaultTheme.minimal2(),
            geomKind = GeomKind.BAR,
            tooltipBehavior1 = tooltipBehavior,
            isYOrientation = false,
            constantsMap = constantsMap,
            renderedAes = varBindings.map(VarBinding::aes) + Aes.WIDTH,
            getOriginalVariableName = { aes -> varBindings.find { it.aes == aes }?.variable?.name }
        ).build()

        val layer = layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)
            .build(data, scaleByAes, scaleMappersNP)


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
