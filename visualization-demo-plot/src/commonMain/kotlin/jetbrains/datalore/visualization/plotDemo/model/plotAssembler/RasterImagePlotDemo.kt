package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
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
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.visualization.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.SharedPieces
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase

open class RasterImagePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
                createPlot(SharedPieces.rasterDataSimple())
        )
    }

    private fun createPlot(data: Map<String, List<*>>): Plot {
        val varX = DataFrame.Variable("x")
        val varY = DataFrame.Variable("y")
        val varFill = DataFrame.Variable("fill")
        val varAlpha = DataFrame.Variable("alpha")
        val builder = DataFrame.Builder()
        for (variable in listOf(varX, varY, varFill, varAlpha)) {
            Preconditions.checkArgument(data.containsKey(variable.name), "Couldn't find input variable " + variable.name)
            builder.put(variable, data[variable.name]!!)
        }
        val df = builder.build()

        val layer = GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(GeomProvider.raster())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                //      .addConstantAes(Aes.ALPHA, 0.5)
                .addBinding(VarBinding(varX, Aes.X, Scales.continuousDomainNumericRange("X")))
                .addBinding(VarBinding(varY, Aes.Y, Scales.continuousDomainNumericRange("Y")))
                .addBinding(VarBinding(varFill, Aes.FILL, ScaleProviderHelper.createDefault(Aes.FILL).createScale(df, varFill)))
                .addBinding(VarBinding(varAlpha, Aes.ALPHA, ScaleProviderHelper.createDefault(Aes.ALPHA).createScale(df, varAlpha)))
                .build(df)

        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        assembler.setTitle("Raster image geometry")
        return assembler.createPlot()
    }
}
