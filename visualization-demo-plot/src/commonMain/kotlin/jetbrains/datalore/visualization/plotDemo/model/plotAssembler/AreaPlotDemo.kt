package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.values.Color
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
import jetbrains.datalore.visualization.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plotDemo.model.Iris
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase

open class AreaPlotDemo: SimpleDemoBase() {
    fun createPlots(): List<Plot> {
        return listOf(sepalLength())
    }

    fun sepalLength(): Plot {
        val xColumn = Iris.sepalLength
        val targetColumn = Iris.target

        val varX = DataFrame.Variable("x")
        val varTarget = DataFrame.Variable("target")
        val data = DataFrame.Builder()
            .putNumeric(varX, xColumn.data.toList())
            .put(varTarget, targetColumn.data.toList())
            .build()

        var scaleTargetColor = Scales.pureDiscrete("Y", listOf("Iris-setosa", "Iris-versicolor", "Iris-virginica"), listOf(Color.RED, Color.GREEN, Color.BLUE),  Color.BLACK)

        val layer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.density())
            .geom(GeomProvider.area())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .groupingVar(varTarget)
            .addBinding(VarBinding(varX, Aes.X, Scales.continuousDomainNumericRange(xColumn.name)))
            .addBinding(VarBinding(Stats.DENSITY, Aes.Y, Scales.continuousDomainNumericRange("")))
            .addBinding(VarBinding(varTarget, Aes.FILL, scaleTargetColor))
            .addConstantAes(Aes.ALPHA, 0.7)
            .build(data)

        val assembler = PlotAssembler.singleTile(listOf(layer), CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        return assembler.createPlot()
    }


}
