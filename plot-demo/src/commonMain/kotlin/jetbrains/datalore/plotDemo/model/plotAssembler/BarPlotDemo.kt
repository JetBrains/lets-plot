/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class BarPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
            simple(),
            grouped(false),         // grouped, dodged
            grouped(true)          // grouped, stacked
//            countStat()
        )
    }

    private fun simple(): jetbrains.datalore.plot.builder.Plot {
        val count = 10
        val a = xValues(count)
        val b = DemoUtil.gauss(count, 12, 0.0, 1.0)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange("A"),
                Aes.Y to Scales.continuousDomainNumericRange("B")
            )
        )

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.bar())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
//                .groupingVar(null)
            .addBinding(
                VarBinding(
                    varA,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varB,
                    Aes.Y
                )
            )
            .addConstantAes(Aes.WIDTH, 0.75)
            .build(data, scaleByAes)
        val assembler = PlotAssembler.singleTile(scaleByAes, listOf(layer), CoordProviders.cartesian(), DefaultTheme())

        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun grouped(stacked: Boolean): jetbrains.datalore.plot.builder.Plot {
        //    boolean stacked = false;
        val count = 10
        //    int groupCount = 2;
        val a = DemoUtil.zip(
            xValues(
                count
            ), xValues(count)
        )
        val b = DemoUtil.zip(DemoUtil.gauss(count, 12, 0.0, 1.0), DemoUtil.gauss(count, 24, 0.0, 1.0))
        val c = DemoUtil.zip(DemoUtil.fill("F", count), DemoUtil.fill("M", count))

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val varC = DataFrame.Variable("C")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .put(varC, c)
            .build()

        val pos = if (stacked)
            PosProvider.barStack()
        else
            PosProvider.dodge()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange("A"),
                Aes.Y to Scales.continuousDomainNumericRange("B"),
                Aes.FILL to colorScale(
                    "C",
                    listOf("F", "M"),
                    listOf(Color.RED, Color.BLUE)
                )
            )
        )

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.bar())
            .pos(pos)
            .groupingVar(varC)
            .addBinding(
                VarBinding(
                    varA,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varB,
                    Aes.Y
                )
            )
            .addBinding(
                VarBinding(
                    varC,
                    Aes.FILL
                )
            )
            .addConstantAes(Aes.WIDTH, if (stacked) 0.75 else 0.9)
            .build(data, scaleByAes)

        val assembler = PlotAssembler.singleTile(
            scaleByAes,
            listOf(layer),
            CoordProviders.cartesian(),
            DefaultTheme()
        )
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    // No more deferred bindings
//    private fun countStat(): jetbrains.datalore.plot.builder.Plot {
//        val count = 100
//        // gen normally distributed numbers in range 0..9 (approximately)
//        val gauss0_9 = DemoUtil.gauss(count, 24, 4.0, 2.0)
//        val a = ArrayList<Double>()
//        for (d in gauss0_9) {
//            a.add(round(d))
//        }
//
//        val varA = DataFrame.Variable("A")
//        val data = DataFrame.Builder()
//            .putNumeric(varA, a)
//            .build()
//
//        val fillScaleProvider = ScaleProviderHelper.create(
//            "count (fill color)",
//            Aes.COLOR,
//            DefaultMapperProviderUtil.createWithDiscreteOutput(
//                listOf(
//                    Color.DARK_BLUE,
//                    Color.DARK_GREEN,
//                    Color.DARK_MAGENTA
//                ), Color.GRAY
//            )
//        )
//
//        val scaleByAes = TypedScaleMap(
//            mapOf(
//                Aes.X to Scales.continuousDomainNumericRange("A"),
//                Aes.Y to Scales.continuousDomainNumericRange(""),
//                Aes.FILL to scaleTargetColor
//            )
//        )
//
//        val layer = GeomLayerBuilder.demoAndTest()
//            .stat(Stats.count())
//            .geom(GeomProvider.bar())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
//            .addBinding(
//                VarBinding(
//                    varA,
//                    Aes.X,
//                    Scales.continuousDomainNumericRange("A")
//                )
//            )
//            .addBinding(VarBinding.deferred(Stats.COUNT, Aes.FILL, fillScaleProvider))
//            .addConstantAes(Aes.WIDTH, .3)
//            .build(data)
//        val assembler = PlotAssembler.singleTile(
//            listOf(layer),
//            CoordProviders.cartesian(),
//            DefaultTheme()
//        )
//
//        assembler.disableInteractions()
//        return assembler.createPlot()
//    }


    companion object {
        private fun xValues(count: Int): List<Double> {
            val values = ArrayList<Double>()
            for (i in 0 until count) {
                values.add(i.toDouble())
            }
            return values
        }

        private fun colorScale(name: String, domain: List<*>, colors: List<Color>): Scale<Color> {
            return Scales.pureDiscrete(name, domain, colors, Color.GRAY)
        }
    }
}
