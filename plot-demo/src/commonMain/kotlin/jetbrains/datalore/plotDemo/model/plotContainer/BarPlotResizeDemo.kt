/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotContainer

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder

class BarPlotResizeDemo private constructor(
    private val sclData: SinCosLineData,
    private val xScale: Scale<*>
) {

    fun createPlotContainer(plotSize: DoubleVector): PlotContainer {
        return PlotContainer(createPlot(), plotSize)
    }

    fun createPlot(): PlotSvgComponent {
        val varX = sclData.varX
        val varY = sclData.varY
        val varCat = sclData.varCat
        val data = sclData.dataFrame

        val categories = ArrayList(data.distinctValues(varCat))
        val colors = listOf(Color.RED, Color.BLUE, Color.CYAN)
        val fillScale = Scales.DemoAndTest.pureDiscrete<Color>("C", categories/*, colors, Color.GRAY*/)
        val fillMapper = Mappers.discrete(
            fillScale.transform as DiscreteTransform,
            colors, Color.GRAY
        )

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to xScale,
                Aes.Y to Scales.DemoAndTest.continuousDomain("sin, cos, line", Aes.Y),
                Aes.FILL to fillScale
            )
        )

        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
            Aes.FILL to fillMapper
        )

        val layerBuilder = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY, PosProvider.dodge())
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.bar())
//            .pos(PosProvider.dodge())
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
            .univariateFunction(GeomTargetLocator.LookupStrategy.NEAREST)
            .build()

        val layer = layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)
            .build(data, scaleByAes, scaleMappersNP)


        //Theme t = new DefaultTheme() {
        //  @Override
        //  public AxisTheme axisX() {
        //    return new DefaultAxisTheme() {
        //      @Override
        //      public boolean showTickLabels() {
        //        return false;
        //      }
        //    };
        //  }
        //};
        val assembler = PlotAssembler.singleTile(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            DefaultTheme.minimal2()
        )
        return assembler.createPlot()
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
                Scales.DemoAndTest.discreteDomain<String>("", sclData.distinctXValues().toList())
            )
        }
    }
}
