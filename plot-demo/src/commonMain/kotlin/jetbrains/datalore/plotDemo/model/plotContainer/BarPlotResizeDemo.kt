/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotContainer

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.theme.DefaultTheme

class BarPlotResizeDemo private constructor(
    private val sclData: SinCosLineData,
    private val xScale: Scale<*>
) {

    fun createPlot(plotSize: ReadableProperty<DoubleVector>): jetbrains.datalore.plot.builder.PlotContainer {
        val varX = sclData.varX
        val varY = sclData.varY
        val varCat = sclData.varCat
        val data = sclData.dataFrame

        val categories = ArrayList(DataFrameUtil.distinctValues(data, varCat))
        val colors = listOf(Color.RED, Color.BLUE, Color.CYAN)
        val fillScale = Scales.pureDiscrete("C", categories, colors, Color.GRAY)


        val layerBuilder = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.bar())
                .pos(PosProvider.dodge())
                .groupingVar(varCat)
                .addBinding(VarBinding(varX, Aes.X, xScale))
                .addBinding(
                    VarBinding(
                        varY,
                        Aes.Y,
                        Scales.continuousDomain("sin, cos, line", Aes.Y)
                    )
                )
                .addBinding(VarBinding(varCat, Aes.FILL, fillScale))
                .addConstantAes(Aes.WIDTH, 0.9)

        // Add bar plot interactions
        val geomInteraction = jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder(
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
                .build(data)


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
        val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(layer), jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
//        assembler.disableInteractions()
        return jetbrains.datalore.plot.builder.PlotContainer(assembler.createPlot(), plotSize)
    }

    companion object {

        fun continuousX(): BarPlotResizeDemo {
            return BarPlotResizeDemo(
                SinCosLineData({ v -> v.toDouble() }, 6),
                Scales.continuousDomain(" ", Aes.X)
            )
        }

        fun discreteX(): BarPlotResizeDemo {
            val sclData =
                SinCosLineData({ v -> "Group label " + (v + 1) }, 6)
            return BarPlotResizeDemo(
                sclData,
                Scales.discreteDomain<String>("", sclData.distinctXValues())
            )
        }
    }
}
