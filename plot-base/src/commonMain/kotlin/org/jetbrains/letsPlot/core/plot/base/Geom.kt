/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

interface Geom {
    val legendKeyElementFactory: LegendKeyElementFactory
    val wontRender: List<Aes<*>> get() = emptyList()
    fun rangeIncludesZero(aes: Aes<*>): Boolean = false
    fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)
}
