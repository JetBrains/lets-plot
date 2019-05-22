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
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.visualization.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase

open class TilePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
                createPlot()
        )
    }

    private fun createPlot(): Plot {
        val valuesX = ArrayList<Double>()
        val valuesY = ArrayList<Double>()
        val valuesV = ArrayList<Double>()
        for (row in 0..3) {
            for (col in 0..9) {
                valuesX.add(col.toDouble())
                valuesY.add(row.toDouble())
                valuesV.add(row.toDouble() + col)
            }
        }

        val varX = DataFrame.Variable("X")
        val varY = DataFrame.Variable("Y")
        val varV = DataFrame.Variable("Value")

        val data = DataFrame.Builder()
                .put(varX, valuesX)
                .put(varY, valuesY)
                .put(varV, valuesV)
                .build()


        //
        // tiles plot layer
        //
        val tilesLayer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.tile())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                //      .addConstantAes(Aes.ALPHA, 0.5)
                .addBinding(VarBinding(varX, Aes.X, Scales.continuousDomainNumericRange("X")))
                .addBinding(VarBinding(varY, Aes.Y, Scales.continuousDomainNumericRange("Y")))
                .addBinding(VarBinding(varV, Aes.FILL, ScaleProviderHelper.createDefault(Aes.FILL).createScale(data, varV)))
                .build(data)

        //
        // Plot
        //
        val assembler = PlotAssembler.singleTile(listOf(tilesLayer),
                CoordProviders.cartesian(), DefaultTheme())
        assembler.setTitle("Tile geometry")
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}
