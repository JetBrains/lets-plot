/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.plotAssembler.ErrorBarPlotDemo.DemoVariant.*
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class ErrorBarPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<PlotSvgComponent> {
        return listOf(
            simple(),
            withDodgePos(),
            blackWithGroup()
        )
    }

    private fun simple(): PlotSvgComponent =
        createPlot(SIMPLE, theme)

    private fun withDodgePos(): PlotSvgComponent =
        createPlot(WITH_DODGE_POS, theme)

    private fun blackWithGroup(): PlotSvgComponent =
        createPlot(BLACK_WITH_GROUP, theme)


    companion object {

        private fun createPlot(demoVariant: DemoVariant, theme: Theme): PlotSvgComponent {
            // sample see: Cookbook for R: www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)
            /*

    #>   supp dose  N   len       sd        se       ci
    #> 1   OJ  0.5 10 13.23 4.459709 1.4102837 3.190283
    #> 2   OJ  1.0 10 22.70 3.910953 1.2367520 2.797727
    #> 3   OJ  2.0 10 26.06 2.655058 0.8396031 1.899314
    #> 4   VC  0.5 10  7.98 2.746634 0.8685620 1.964824
    #> 5   VC  1.0 10 16.77 2.515309 0.7954104 1.799343
    #> 6   VC  2.0 10 26.14 4.797731 1.5171757 3.432090

     */

            val varSupp = DataFrame.Variable("supp")
            val varDose = DataFrame.Variable("dose")
            val varN = DataFrame.Variable("N")
            val varLen = DataFrame.Variable("len")
            val varSD = DataFrame.Variable("sd")
            val varSE = DataFrame.Variable("se")
            val varCI = DataFrame.Variable("ci")

            var data = DataFrame.Builder()
                .put(varSupp, listOf("OJ", "OJ", "OJ", "VC", "VC", "VC"))
                .put(varDose, listOf(0.5, 1.0, 2.0, 0.5, 1.0, 2.0))
                .put(varN, listOf(10.0, 10.0, 10.0, 10.0, 10.0, 10.0))
                .put(varLen, listOf(13.23, 22.70, 26.06, 7.98, 16.77, 26.14))
                .put(varSD, listOf(4.459709, 3.910953, 2.655058, 2.746634, 2.515309, 4.797731))
                .put(varSE, listOf(1.4102837, 1.236752, 0.8396031, 0.868562, 0.7954104, 1.5171757))
                .put(varCI, listOf(3.190283, 2.797727, 1.899314, 1.964824, 1.799343, 3.43209))
                .build()

            val colorScale = Scales.DemoAndTest.pureDiscrete<Color>(
                "Supplement",
                domainValues = data[varSupp].filterNotNull(),
//                outputValues = listOf(Color.ORANGE, Color.DARK_GREEN),
//                defaultOutputValue = Color.GRAY
            )
            val colorMapper = Mappers.discrete(
                colorScale.transform as DiscreteTransform,
                listOf(Color.ORANGE, Color.DARK_GREEN),
                Color.GRAY
            )


            var pos = PosProvider.wrap(PositionAdjustments.identity())
            if (demoVariant == WITH_DODGE_POS || demoVariant == BLACK_WITH_GROUP) {
                pos = PosProvider.dodge(0.1)
            }

            //
            // error bars layer
            //
            val varYMin = DataFrame.Variable.createOriginal("ymin")
            val varYMax = DataFrame.Variable("ymax")
            data = data.builder()
                .put(varYMin, DemoUtil.sub(data.getNumeric(varLen), data.getNumeric(varSE)))
                .put(varYMax, DemoUtil.add(data.getNumeric(varLen), data.getNumeric(varSE)))
                .build()

            val scaleByAes = TypedScaleMap(
                mapOf(
                    Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("Dose (mg)"),
                    Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("Tooth length"),
                    Aes.YMIN to Scales.DemoAndTest.continuousDomainNumericRange("Y min"),
                    Aes.YMAX to Scales.DemoAndTest.continuousDomainNumericRange("Y max"),
                    Aes.COLOR to colorScale
                )
            )

            val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
                Aes.COLOR to colorMapper
            )


            val layerBuilder = GeomLayerBuilder.demoAndTest(GeomProvider.errorBar(), Stats.IDENTITY, pos)
//                .stat(Stats.IDENTITY)
//                .geom(GeomProvider.errorBar())
//                .pos(pos)
                .addBinding(
                    VarBinding(
                        varDose,
                        Aes.X
                    )
                )
                .addBinding(
                    VarBinding(
                        varYMin,
                        Aes.YMIN
                    )
                )
                .addBinding(
                    VarBinding(
                        varYMax,
                        Aes.YMAX
                    )
                )
                .addConstantAes(Aes.WIDTH, 0.1)

            when (demoVariant) {
                SIMPLE, WITH_DODGE_POS -> layerBuilder.addBinding(
                    VarBinding(
                        varSupp,
                        Aes.COLOR
                    )
                )

                BLACK_WITH_GROUP -> {
                    layerBuilder.addConstantAes(Aes.COLOR, Color.BLACK)
                    layerBuilder.groupingVar(varSupp)
                }
            }
            val errorBarsLayer = layerBuilder.build(data, scaleByAes, scaleMappersNP)

            //
            // lines layer
            //
            val linesLayer = GeomLayerBuilder.demoAndTest(GeomProvider.line(), Stats.IDENTITY, pos)
//                .stat(Stats.IDENTITY)
//                .geom(GeomProvider.line())
//                .pos(pos)
                .addBinding(
                    VarBinding(
                        varDose,
                        Aes.X
                    )
                )
                .addBinding(
                    VarBinding(
                        varLen,
                        Aes.Y
                    )
                )
                .addBinding(VarBinding(varSupp, Aes.COLOR))
                .build(data, scaleByAes, scaleMappersNP)

            //
            // points layer
            //
            val pointsLayer = GeomLayerBuilder.demoAndTest(GeomProvider.point(), Stats.IDENTITY, pos)
//                .stat(Stats.IDENTITY)
//                .geom(GeomProvider.point())
//                .pos(pos)
                .addBinding(
                    VarBinding(
                        varDose,
                        Aes.X
                    )
                )
                .addBinding(
                    VarBinding(
                        varLen,
                        Aes.Y
                    )
                )
                .addBinding(VarBinding(varSupp, Aes.COLOR))
                .addConstantAes(Aes.SIZE, 5.0)
                .build(data, scaleByAes, scaleMappersNP)

            //
            // Plot
            //
            val assembler = PlotAssembler.singleTile(
                listOf(
                    errorBarsLayer,
                    linesLayer,
                    pointsLayer
                ),
                scaleByAes,
                scaleMappersNP,
                CoordProviders.cartesian(),
                theme
            )
            assembler.title = "Error Bars"
            assembler.disableInteractions()
            return assembler.createPlot()
        }
    }

    internal enum class DemoVariant {
        SIMPLE,
        WITH_DODGE_POS,
        BLACK_WITH_GROUP
    }
}
