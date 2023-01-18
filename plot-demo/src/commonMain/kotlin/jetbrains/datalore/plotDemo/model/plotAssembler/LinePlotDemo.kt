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
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class LinePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<PlotSvgComponent> {
        return listOf(
            simple(),
            grouped(),
            notGrouped()
        )
    }

    private fun simple(): PlotSvgComponent = createSimplePlot()

    private fun grouped(): PlotSvgComponent = createGroupedLinePlot(true)

    private fun notGrouped(): PlotSvgComponent = createGroupedLinePlot(false)

    private fun createSimplePlot(): PlotSvgComponent {
        val count = 100
        val a = xValues(count)
        val b = yValues(count, 32)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
                Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B")
            )
        )

        val layer = GeomLayerBuilder.demoAndTest(GeomProvider.path(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
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

    private fun createGroupedLinePlot(grouped: Boolean): PlotSvgComponent {
        //    boolean grouped = false;
        val count = 100 / 2
        val a = DemoUtil.zip(
            xValues(
                count
            ), xValues(count)
        )
        val b = DemoUtil.zip(
            yValues(
                count,
                32
            ), yValues(count, 64)
        )
        val c = DemoUtil.zip(
            fill(
                "F",
                count
            ), fill("M", count)
        )

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val varC = DataFrame.Variable("C")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .put(varC, c)
            .build()

        val colorScale = Scales.DemoAndTest.pureDiscrete(
            "C",
            listOf("F", "M"),
//                    listOf(Color.RED, Color.BLUE),
//                    Color.GRAY
        )
        val colorMapper = Mappers.discrete(
            colorScale.transform as DiscreteTransform,
            listOf(Color.RED, Color.BLUE), Color.GRAY
        )

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
                Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B"),
                Aes.COLOR to colorScale
            )
        )

        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
            Aes.COLOR to colorMapper
        )

        val layer = with(GeomLayerBuilder.demoAndTest(GeomProvider.path(), Stats.IDENTITY)) {
//            stat(Stats.IDENTITY)
//            geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path())
//            pos(PosProvider.wrap(PositionAdjustments.identity()))
            if (grouped) {
                groupingVar(varC)
            }
            addBinding(
                VarBinding(
                    varA,
                    Aes.X
                )
            )
            addBinding(
                VarBinding(
                    varB,
                    Aes.Y
                )
            )
            addBinding(
                VarBinding(
                    varC,
                    Aes.COLOR
                )
            )
            build(data, scaleByAes, scaleMappersNP)
        }

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

        private fun yValues(count: Int, seed: Long): List<Double> {
            val gauss = DemoUtil.gauss(count, seed, 0.0, 0.2 * count)
            val values = ArrayList<Double>()
            for (i in count - 1 downTo 0) {
                values.add(gauss[i] + i)
            }
            return values
        }

        private fun <T> fill(v: T, count: Int): List<T> {
            val l = ArrayList<T>()
            for (i in 0 until count) {
                l.add(v)
            }
            return l
        }

    }
}
