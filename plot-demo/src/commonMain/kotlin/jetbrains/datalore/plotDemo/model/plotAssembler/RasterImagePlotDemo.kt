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
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SharedPieces
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

open class RasterImagePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
            createPlot(SharedPieces.rasterData_Blue())
        )
    }

    private fun createPlot(data: Map<String, List<*>>): Plot {
        val varX = DataFrame.Variable("x")
        val varY = DataFrame.Variable("y")
        val varFill = DataFrame.Variable("fill")
        val varAlpha = DataFrame.Variable("alpha")
        val builder = DataFrame.Builder()
        for (variable in listOf(varX, varY, varFill, varAlpha)) {
            Preconditions.checkArgument(
                data.containsKey(variable.name),
                "Couldn't find input variable " + variable.name
            )
            builder.put(variable, data.getValue(variable.name))
        }
        val df = builder.build()
        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange("X"),
                Aes.Y to Scales.continuousDomainNumericRange("Y"),
                Aes.FILL to ScaleProviderHelper.createDefault(Aes.FILL).createScale(
                    varFill.label,
                    df.range(varFill)!!
                ),
                Aes.ALPHA to ScaleProviderHelper.createDefault(Aes.ALPHA).createScale(
                    varAlpha.label,
                    df.range(varAlpha)!!
                )
            )
        )

        val layer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.raster())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            //      .addConstantAes(Aes.ALPHA, 0.5)
            .addBinding(VarBinding(varX, Aes.X))
            .addBinding(VarBinding(varY, Aes.Y))
            .addBinding(VarBinding(varFill, Aes.FILL))
            .addBinding(VarBinding(varAlpha, Aes.ALPHA))
            .build(df, scaleByAes)

        val assembler = PlotAssembler.singleTile(
            scaleByAes,
            listOf(layer),
            CoordProviders.cartesian(),
            DefaultTheme()
        )
        assembler.disableInteractions()
        assembler.setTitle("Raster image geometry")
        return assembler.createPlot()
    }
}
