package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.base.stat.Stats
import jetbrains.datalore.visualization.plot.builder.Plot
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.visualization.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.visualization.plot.builder.assemble.PosProvider
import jetbrains.datalore.visualization.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.builder.coord.CoordProviders
import jetbrains.datalore.visualization.plot.builder.scale.DefaultMapperProviderUtil
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.visualization.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil
import kotlin.math.round

open class BarPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
                simple(),
                grouped(false),         // grouped, dodged
                grouped(true),          // grouped, stacked
                countStat()
        )
    }

    private fun simple(): Plot {
        val count = 10
        val a = xValues(count)
        val b = DemoUtil.gauss(count, 12, 0.0, 1.0)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .build()

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.bar())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
//                .groupingVar(null)
                .addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varB, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .addConstantAes(Aes.WIDTH, 0.75)
                .build(data)
        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())

        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun grouped(stacked: Boolean): Plot {
        //    boolean stacked = false;
        val count = 10
        //    int groupCount = 2;
        val a = DemoUtil.zip(xValues(count), xValues(count))
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

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.bar())
                .pos(pos)
                .groupingVar(varC)
                .addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varB, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .addBinding(VarBinding(varC, Aes.FILL, colorScale("C", listOf("F", "M"), listOf(Color.RED, Color.BLUE))))
                .addConstantAes(Aes.WIDTH, if (stacked) 0.75 else 0.9)
                .build(data)

        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun countStat(): Plot {
        val count = 100
        // gen normally distributed numbers in range 0..9 (approximately)
        val gauss0_9 = DemoUtil.gauss(count, 24, 4.0, 2.0)
        val a = ArrayList<Double>()
        for (d in gauss0_9) {
            a.add(round(d))
        }

        val varA = DataFrame.Variable("A")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .build()

        val fillScaleProvider = ScaleProviderHelper.create(
                "count (fill color)",
                Aes.COLOR,
                DefaultMapperProviderUtil.createWithDiscreteOutput(listOf(Color.DARK_BLUE, Color.DARK_GREEN, Color.DARK_MAGENTA), Color.GRAY))

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.count())
                .geom(GeomProvider.bar())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding.deferred(Stats.COUNT, Aes.FILL, fillScaleProvider))
                .addConstantAes(Aes.WIDTH, .3)
                .build(data)
        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())

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

        private fun colorScale(name: String, domain: List<*>, colors: List<Color>): Scale<Color> {
            return Scales.pureDiscrete(name, domain, colors, Color.GRAY)
        }
    }
}
