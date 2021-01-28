/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme


interface Theme {
    fun axisX(): AxisTheme

    fun axisY(): AxisTheme

    fun legend(): LegendTheme

    fun facets(): FacetsTheme
}
