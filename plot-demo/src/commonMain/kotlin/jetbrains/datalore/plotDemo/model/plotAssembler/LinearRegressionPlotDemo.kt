/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.random.RandomGaussian
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import kotlin.random.Random

// Uses server-side stat methods 
open class LinearRegressionPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO


    fun createPlots(): List<jetbrains.datalore.plot.builder.PlotSvgComponent> {
        return listOf(
            createPlot()
        )
    }

    private fun createPlot(): jetbrains.datalore.plot.builder.PlotSvgComponent {
        val count = 10

        //
        // scatter
        //
        var listsXY =
            genObservations(count)
        var valuesX = listsXY[0]
        var valuesY = listsXY[1]
        val varOrigX = DataFrame.Variable("A")
        val varOrigY = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varOrigX, valuesX)
            .putNumeric(varOrigY, valuesY)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
                Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B")
            )
        )

        val scatterLayer = GeomLayerBuilder.demoAndTest(GeomProvider.point(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.point())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X,
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y,
                )
            )
            .build(data, scaleByAes, emptyMap())

        //
        // true line
        //
        listsXY = trueLineModel()
        valuesX = listsXY[0]
        valuesY = listsXY[1]

        val varTLX = DataFrame.Variable("true X")
        val varTLY = DataFrame.Variable("true Y")
        val line = DataFrame.Builder()
            .putNumeric(varTLX, valuesX)
            .putNumeric(varTLY, valuesY)
            .build()
        val trueLineLayer = GeomLayerBuilder.demoAndTest(GeomProvider.line(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(GeomProvider.line())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varTLX,
                    Aes.X,
                )
            )
            .addBinding(
                VarBinding(
                    varTLY,
                    Aes.Y,
                )
            )
            .addConstantAes(Aes.LINETYPE, NamedLineType.DASHED)
            .build(line, scaleByAes, emptyMap())

        //
        // Smooth stat (regression)
        //
        val regressionLineLayer = GeomLayerBuilder.demoAndTest(GeomProvider.smooth(), Stats.smooth())
//            .stat(Stats.smooth())
//            .geom(GeomProvider.smooth())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X,
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y,
                )
            )
            .build(data, scaleByAes, emptyMap())

        //
        // Smooth stat - standard error
        //
        val seLineLayer = GeomLayerBuilder.demoAndTest(GeomProvider.point(), Stats.smooth())
//            .stat(Stats.smooth())
//            .geom(GeomProvider.point())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X,
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y,
                )
            )
            .addBinding(VarBinding(Stats.SE, Aes.Y))
            .addConstantAes(Aes.COLOR, Color.RED)
            .build(data, scaleByAes, emptyMap())

        //
        // Plot
        //
        val assembler = PlotAssembler.demoAndTest(
            listOf(
                scatterLayer,
                trueLineLayer,
                regressionLineLayer,
                seLineLayer
            ),
            scaleByAes,
            emptyMap(),
            CoordProviders.cartesian(), theme
        )
        assembler.title = "Linear Regression"
        assembler.disableInteractions()
        return assembler.createPlot()
    }


    companion object {

        private const val SEED: Long = 27
        private const val INTERCEPT = 0.0
        private const val SLOPE = 0.5

        private const val XMIN = 0.0
        private const val XMAX = 10.0

        private fun genObservations(n: Int): List<List<Double>> {
            val valuesX = ArrayList<Double>()
            val valuesY = ArrayList<Double>()
            val randomX = Random(SEED)
            val randomY = RandomGaussian(Random(SEED))
            for (i in 0 until n) {
                val x = XMIN + randomX.nextDouble() * (XMAX - XMIN)
                val y = INTERCEPT + SLOPE * x + randomY.nextGaussian()
                valuesX.add(x)
                valuesY.add(y)
            }

            return listOf(valuesX, valuesY)
        }

        private fun trueLineModel(): List<List<Double>> {
            val x0 = XMIN
            val y0 = INTERCEPT + SLOPE * x0
            val x1 = XMAX
            val y1 = INTERCEPT + SLOPE * x1

            val valuesX = listOf(x0, x1)
            val valuesY = listOf(y0, y1)

            return listOf(valuesX, valuesY)
        }

    }
}
