package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Stats
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.gog.core.scale.Scales
import jetbrains.datalore.visualization.plot.gog.plot.Plot
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.GeomLayerBuilder
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PlotAssembler
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider
import jetbrains.datalore.visualization.plot.gog.plot.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.gog.plot.coord.CoordProviders
import jetbrains.datalore.visualization.plot.gog.plot.theme.DefaultTheme
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
