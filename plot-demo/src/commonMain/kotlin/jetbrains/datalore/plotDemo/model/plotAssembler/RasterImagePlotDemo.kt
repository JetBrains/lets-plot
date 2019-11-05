/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SharedPieces
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

open class RasterImagePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
                createPlot(SharedPieces.rasterDataSimple())
        )
    }

    private fun createPlot(data: Map<String, List<*>>): jetbrains.datalore.plot.builder.Plot {
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

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
                .stat(Stats.IDENTITY)
                .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.raster())
                .pos(PosProvider.wrap(PositionAdjustments.identity()))
                //      .addConstantAes(Aes.ALPHA, 0.5)
                .addBinding(
                    VarBinding(
                        varX,
                        Aes.X,
                        Scales.continuousDomainNumericRange("X")
                    )
                )
                .addBinding(
                    VarBinding(
                        varY,
                        Aes.Y,
                        Scales.continuousDomainNumericRange("Y")
                    )
                )
                .addBinding(
                    VarBinding(
                        varFill,
                        Aes.FILL,
                        ScaleProviderHelper.createDefault(Aes.FILL).createScale(
                            df,
                            varFill
                        )
                    )
                )
                .addBinding(
                    VarBinding(
                        varAlpha,
                        Aes.ALPHA,
                        ScaleProviderHelper.createDefault(Aes.ALPHA).createScale(
                            df,
                            varAlpha
                        )
                    )
                )
                .build(df)

        val assembler = jetbrains.datalore.plot.builder.assemble.PlotAssembler.singleTile(listOf(layer), jetbrains.datalore.plot.builder.coord.CoordProviders.cartesian(), DefaultTheme())
        assembler.disableInteractions()
        assembler.setTitle("Raster image geometry")
        return assembler.createPlot()
    }
}
