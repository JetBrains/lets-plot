/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.geom.util.LabelOptions
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper

class LabelRepelGeom : TextRepelGeom() {
    val labelOptions = LabelOptions()

    override fun getTextHelper(
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ): TextHelper {
        return TextHelper(aesthetics, pos, coord, ctx)
            .setLabelOptions(labelOptions)
            .setFormatter(formatter)
            .setNaValue(naValue)
            .setSizeUnit(sizeUnit)
            .setCheckOverlap(checkOverlap)
    }
}