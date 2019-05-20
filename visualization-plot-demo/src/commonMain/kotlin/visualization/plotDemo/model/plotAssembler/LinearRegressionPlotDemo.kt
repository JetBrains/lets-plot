package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.random.RandomGaussian
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.stat.Stats
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.visualization.plot.base.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.gog.plot.Plot
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.GeomLayerBuilder
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PlotAssembler
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider
import jetbrains.datalore.visualization.plot.gog.plot.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProviders
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProviderHelper
import jetbrains.datalore.visualization.plot.gog.plot.theme.DefaultTheme
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.StatsServerSide
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import kotlin.random.Random

// Uses server-side stat methods 
open class LinearRegressionPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO


    fun createPlots(): List<Plot> {
        return listOf(
                createPlot()
        )
    }

    private fun createPlot(): Plot {
        val count = 10

        //
        // scatter
        //
        var listsXY = genObservations(count)
        var valuesX = listsXY[0]
        var valuesY = listsXY[1]
        val varOrigX = DataFrame.Variable("A")
        val varOrigY = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varOrigX, valuesX)
                .putNumeric(varOrigY, valuesY)
                .build()

        val scatterLayer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.point())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varOrigX, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varOrigY, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .build(data)

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
        val trueLineLayer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.line())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varTLX, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varTLY, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .addConstantAes(Aes.LINETYPE, NamedLineType.DASHED)
                .build(line)


        //
        // Smooth stat (regression)
        //
        val regressionLineLayer = GeomLayerBuilder.demoAndTest()
                .stat(StatsServerSide.smooth())
                .geom(GeomProvider.smooth())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varOrigX, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varOrigY, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .build(data)

        //
        // Smooth stat - standard error
        //
        val seLineLayer = GeomLayerBuilder.demoAndTest()
                .stat(StatsServerSide.smooth())
                .geom(GeomProvider.point())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varOrigX, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varOrigY, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .addBinding(VarBinding.deferred(Stats.SE, Aes.Y, ScaleProviderHelper.createDefault(Aes.Y)))
                .addConstantAes(Aes.COLOR, Color.RED)
                .build(data)

        //
        // Plot
        //
        val assembler = PlotAssembler.singleTile(listOf(
                scatterLayer,
                trueLineLayer,
                regressionLineLayer,
                seLineLayer
        ), CoordProviders.cartesian(), DefaultTheme())
        assembler.setTitle("Linear Regression")
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
