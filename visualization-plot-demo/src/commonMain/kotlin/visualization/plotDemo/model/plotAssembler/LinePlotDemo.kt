package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.stat.Stats
import jetbrains.datalore.visualization.plot.base.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.builder.Plot
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.visualization.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.visualization.plot.builder.assemble.PosProvider
import jetbrains.datalore.visualization.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.builder.coord.CoordProviders
import jetbrains.datalore.visualization.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil

open class LinePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
                simple(),
                grouped(),
                notGrouped()
        )
    }

    private fun simple(): Plot = createSimplePlot()

    private fun grouped(): Plot = createGroupedLinePlot(true)

    private fun notGrouped(): Plot = createGroupedLinePlot(false)

    private fun createSimplePlot(): Plot {
        val count = 100
        val a = xValues(count)
        val b = yValues(count, 32)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .build()

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.path())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varB, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .build(data)

        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun createGroupedLinePlot(grouped: Boolean): Plot {
        //    boolean grouped = false;
        val count = 100 / 2
        val a = DemoUtil.zip(xValues(count), xValues(count))
        val b = DemoUtil.zip(yValues(count, 32), yValues(count, 64))
        val c = DemoUtil.zip(fill("F", count), fill("M", count))

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val varC = DataFrame.Variable("C")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .put(varC, c)
                .build()

        val layer = with(GeomLayerBuilder.demoAndTest()) {
            stat(Stats.IDENTITY)
            geom(GeomProvider.path())
            pos(PosProvider.wrap(PositionAdjustments.identity()))
            if (grouped) {
                groupingVar(varC)
            }
            addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
            addBinding(VarBinding(varB, Aes.Y, Scales.continuousDomainNumericRange("B")))
            addBinding(VarBinding(varC, Aes.COLOR, Scales.pureDiscrete("C", listOf("F", "M"), listOf(Color.RED, Color.BLUE), Color.GRAY)))
            build(data)
        }

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
