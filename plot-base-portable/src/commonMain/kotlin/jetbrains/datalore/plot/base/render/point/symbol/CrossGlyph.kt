/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.cos

internal class CrossGlyph @JvmOverloads constructor(location: DoubleVector, size: Double, inscribedInCircle: Boolean = true) : TwoShapeGlyph() {

    init {
        val cx = location.x
        val cy = location.y
        val w = if (inscribedInCircle)
            size * CIRCLE_WIDTH_ADJUST_RATIO
        else
            size
        val half = w / 2 // half width of inner square

        val backSlashLine = SvgSlimElements.line(
                cx - half,
                cy - half,
                cx + half,
                cy + half)
        val slashLine = SvgSlimElements.line(
                cx - half,
                cy + half,
                cx + half,
                cy - half)

        setShapes(backSlashLine, slashLine)
    }

    companion object {
        val CIRCLE_WIDTH_ADJUST_RATIO = cos(PI / 4)   // cos(45)
    }
}
