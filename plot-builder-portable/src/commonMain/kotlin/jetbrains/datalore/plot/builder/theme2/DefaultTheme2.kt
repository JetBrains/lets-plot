/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.plot.builder.theme.*

class DefaultTheme2(
    private val options: Map<String, Any>
) : Theme {

    private val axisX = DefaultAxisTheme("x", options)
    private val axisY = DefaultAxisTheme("y", options)
    private val legend = DefaultLegendTheme(options)


    override fun axisX(): AxisTheme = axisX

    override fun axisY(): AxisTheme = axisY

    override fun legend(): LegendTheme = legend

    override fun facets(): FacetsTheme {
        return OLD_THEME.facets()
    }

    override fun plot(): PlotTheme {
        return OLD_THEME.plot()
    }

    override fun multiTile(): Theme {
        return OLD_THEME.multiTile()
    }

    companion object {
        @Suppress("RemoveRedundantQualifierName")
        private val OLD_THEME = jetbrains.datalore.plot.builder.theme.DefaultTheme()
    }
}