package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class ScatterPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
                createPlot()
        )
    }

    private fun createPlot(): jetbrains.datalore.plot.builder.Plot {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .build()

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point())
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

        val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(layer),
                jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
        assembler.setTitle("Scatter plot")
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}
