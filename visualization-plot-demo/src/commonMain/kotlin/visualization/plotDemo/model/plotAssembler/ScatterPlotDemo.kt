package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
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

open class ScatterPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
                createPlot()
        )
    }

    private fun createPlot(): Plot {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
                .putNumeric(varA, a)
                .putNumeric(varB, b)
                .build()

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.point())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                .addBinding(VarBinding(varA, Aes.X, Scales.continuousDomainNumericRange("A")))
                .addBinding(VarBinding(varB, Aes.Y, Scales.continuousDomainNumericRange("B")))
                .build(data)

        val assembler = PlotAssembler.singleTile(listOf(layer),
                CoordProviders.cartesian(), DefaultTheme())
        assembler.setTitle("Scatter plot")
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}
