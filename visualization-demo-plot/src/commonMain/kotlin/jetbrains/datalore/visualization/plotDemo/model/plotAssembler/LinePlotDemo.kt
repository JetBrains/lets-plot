package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil

open class LinePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
                simple(),
                grouped(),
                notGrouped()
        )
    }

    private fun simple(): jetbrains.datalore.plot.builder.Plot = createSimplePlot()

    private fun grouped(): jetbrains.datalore.plot.builder.Plot = createGroupedLinePlot(true)

    private fun notGrouped(): jetbrains.datalore.plot.builder.Plot = createGroupedLinePlot(false)

    private fun createSimplePlot(): jetbrains.datalore.plot.builder.Plot {
        val count = 100
        val a = xValues(count)
        val b = yValues(count, 32)

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .build()

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(
                    VarBinding(
                        varA,
                        Aes.X,
                        Scales.continuousDomainNumericRange("A")
                    )
                )
                .addBinding(
                    VarBinding(
                        varB,
                        Aes.Y,
                        Scales.continuousDomainNumericRange("B")
                    )
                )
                .build(data)

        val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(layer), jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun createGroupedLinePlot(grouped: Boolean): jetbrains.datalore.plot.builder.Plot {
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

        val layer = with(jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()) {
            stat(Stats.IDENTITY)
            geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path())
            pos(PosProvider.wrap(PositionAdjustments.identity()))
            if (grouped) {
                groupingVar(varC)
            }
            addBinding(
                VarBinding(
                    varA,
                    Aes.X,
                    Scales.continuousDomainNumericRange("A")
                )
            )
            addBinding(
                VarBinding(
                    varB,
                    Aes.Y,
                    Scales.continuousDomainNumericRange("B")
                )
            )
            addBinding(
                VarBinding(
                    varC,
                    Aes.COLOR,
                    Scales.pureDiscrete("C", listOf("F", "M"), listOf(Color.RED, Color.BLUE), Color.GRAY)
                )
            )
            build(data)
        }

        val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(layer), jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
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
