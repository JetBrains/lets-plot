package jetbrains.datalore.visualization.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.scale.Mappers
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.base.stat.Stats
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.visualization.plotDemo.model.plotAssembler.ErrorBarPlotDemo.DemoVariant.*
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil

open class ErrorBarPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
                simple(),
                withDodgePos(),
                blackWithGroup()
        )
    }

    private fun simple(): jetbrains.datalore.plot.builder.Plot = createPlot(SIMPLE)

    private fun withDodgePos(): jetbrains.datalore.plot.builder.Plot = createPlot(WITH_DODGE_POS)

    private fun blackWithGroup(): jetbrains.datalore.plot.builder.Plot = createPlot(BLACK_WITH_GROUP)


    companion object {

        private fun createPlot(demoVariant: DemoVariant): jetbrains.datalore.plot.builder.Plot {
            // sample see: Cookbook for R: http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)
            /*

    #>   supp dose  N   len       sd        se       ci
    #> 1   OJ  0.5 10 13.23 4.459709 1.4102837 3.190283
    #> 2   OJ  1.0 10 22.70 3.910953 1.2367520 2.797727
    #> 3   OJ  2.0 10 26.06 2.655058 0.8396031 1.899314
    #> 4   VC  0.5 10  7.98 2.746634 0.8685620 1.964824
    #> 5   VC  1.0 10 16.77 2.515309 0.7954104 1.799343
    #> 6   VC  2.0 10 26.14 4.797731 1.5171757 3.432090

     */

            val varSupp = DataFrame.Variable("supp")
            val varDose = DataFrame.Variable("dose")
            val varN = DataFrame.Variable("N")
            val varLen = DataFrame.Variable("len")
            val varSD = DataFrame.Variable("sd")
            val varSE = DataFrame.Variable("se")
            val varCI = DataFrame.Variable("ci")

            var data = DataFrame.Builder()
                    .put(varSupp, listOf("OJ", "OJ", "OJ", "VC", "VC", "VC"))
                    .put(varDose, listOf(0.5, 1.0, 2.0, 0.5, 1.0, 2.0))
                    .put(varN, listOf(10.0, 10.0, 10.0, 10.0, 10.0, 10.0))
                    .put(varLen, listOf(13.23, 22.70, 26.06, 7.98, 16.77, 26.14))
                    .put(varSD, listOf(4.459709, 3.910953, 2.655058, 2.746634, 2.515309, 4.797731))
                    .put(varSE, listOf(1.4102837, 1.236752, 0.8396031, 0.868562, 0.7954104, 1.5171757))
                    .put(varCI, listOf(3.190283, 2.797727, 1.899314, 1.964824, 1.799343, 3.43209))
                    .build()

            val colorScale = Scales.discreteDomain("Supplement",
                    data[varSupp],
                    Mappers.discrete(listOf(Color.ORANGE, Color.DARK_GREEN), Color.GRAY)
            )


            var pos = PosProvider.wrap(PositionAdjustments.identity())
            if (demoVariant == WITH_DODGE_POS || demoVariant == BLACK_WITH_GROUP) {
                pos = PosProvider.dodge(0.1)
            }

            //
            // error bars layer
            //
            val varYMin = DataFrame.Variable.createOriginal("ymin")
            val varYMax = DataFrame.Variable("ymax")
            data = data.builder()
                    .put(varYMin, DemoUtil.sub(data.getNumeric(varLen), data.getNumeric(varSE)))
                    .put(varYMax, DemoUtil.add(data.getNumeric(varLen), data.getNumeric(varSE)))
                    .build()

            val layerBuilder = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                    .stat(Stats.IDENTITY)
                    .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.errorBar())
                    .pos(pos)
                    .addBinding(
                        VarBinding(
                            varDose,
                            Aes.X,
                            Scales.continuousDomainNumericRange("Dose (mg)")
                        )
                    )
                    .addBinding(
                        VarBinding(
                            varYMin,
                            Aes.YMIN,
                            Scales.continuousDomainNumericRange("Y min")
                        )
                    )
                    .addBinding(
                        VarBinding(
                            varYMax,
                            Aes.YMAX,
                            Scales.continuousDomainNumericRange("Y max")
                        )
                    )
                    .addConstantAes(Aes.WIDTH, 0.1)

            when (demoVariant) {
                SIMPLE, WITH_DODGE_POS -> layerBuilder.addBinding(
                    VarBinding(
                        varSupp,
                        Aes.COLOR,
                        colorScale
                    )
                )
                BLACK_WITH_GROUP -> {
                    layerBuilder.addConstantAes(Aes.COLOR, Color.BLACK)
                    layerBuilder.groupingVar(varSupp)
                }
            }
            val errorBarsLayer = layerBuilder.build(data)

            //
            // lines layer
            //
            val linesLayer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                    .stat(Stats.IDENTITY)
                    .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.line())
                    .pos(pos)
                    .addBinding(
                        VarBinding(
                            varDose,
                            Aes.X,
                            Scales.continuousDomainNumericRange("Dose (mg)")
                        )
                    )
                    .addBinding(
                        VarBinding(
                            varLen,
                            Aes.Y,
                            Scales.continuousDomainNumericRange("Tooth length")
                        )
                    )
                    .addBinding(VarBinding(varSupp, Aes.COLOR, colorScale))
                    .build(data)

            //
            // points layer
            //
            val pointsLayer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                    .stat(Stats.IDENTITY)
                    .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point())
                    .pos(pos)
                    .addBinding(
                        VarBinding(
                            varDose,
                            Aes.X,
                            Scales.continuousDomainNumericRange("Dose (mg)")
                        )
                    )
                    .addBinding(
                        VarBinding(
                            varLen,
                            Aes.Y,
                            Scales.continuousDomainNumericRange("Tooth length")
                        )
                    )
                    .addBinding(VarBinding(varSupp, Aes.COLOR, colorScale))
                    .addConstantAes(Aes.SIZE, 5.0)
                    .build(data)

            //
            // Plot
            //
            val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(
                    errorBarsLayer,
                    linesLayer,
                    pointsLayer
            ), jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
            assembler.setTitle("Error Bars")
            assembler.disableInteractions()
            return assembler.createPlot()
        }
    }

    internal enum class DemoVariant {
        SIMPLE,
        WITH_DODGE_POS,
        BLACK_WITH_GROUP
    }
}
