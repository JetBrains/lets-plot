/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class BarPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<PlotSvgComponent> {
        return listOf(
            simple(),
            grouped(false),         // grouped, dodged
            grouped(true)          // grouped, stacked
        )
    }

    private fun simple(): PlotSvgComponent {
        val count = 10
        val a = xValues(count)
        val b = DemoUtil.gauss(count, 12, 0.0, 1.0)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()

        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
            Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B")
        )

        val layer = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.bar())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
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
            .build(data, scaleByAes, emptyMap())

        val assembler = PlotAssembler.demoAndTest(
            listOf(layer),
            scaleByAes,
            emptyMap(),
            CoordProviders.cartesian(), theme
        )

        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun grouped(stacked: Boolean): PlotSvgComponent {
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

        val scaleColor = colorScale(
            "C",
            listOf("F", "M"),
//                    listOf(Color.RED, Color.BLUE)
        )
        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
            Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B"),
            Aes.FILL to scaleColor
        )
        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
            Aes.FILL to Mappers.discrete(
                scaleColor.transform as DiscreteTransform,
                listOf(Color.RED, Color.BLUE),
                Color.GRAY
            )
        )

        val layer = GeomLayerBuilder.demoAndTest(GeomProvider.bar(), Stats.IDENTITY, pos)
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.bar())
//            .pos(pos)
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
            .build(data, scaleByAes, scaleMappersNP)

        val assembler = PlotAssembler.demoAndTest(
            listOf(layer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(),
            theme
        )
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    companion object {
        private fun xValues(count: Int): List<Double> {
            val values = ArrayList<Double>()
            for (i in 0 until count) {
                values.add(i.toDouble())
            }
            return values
        }

        private fun colorScale(name: String, domain: List<Any>/*, colors: List<Color>*/): Scale {
            return Scales.DemoAndTest.pureDiscrete(name, domain/*, colors, Color.GRAY*/)
        }
    }
}
