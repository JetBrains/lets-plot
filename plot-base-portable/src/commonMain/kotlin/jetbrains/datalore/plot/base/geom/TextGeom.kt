/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.common.data.SeriesUtil

class TextGeom : GeomBase() {
    var label_format = DEF_LABEL_FORMAT
        set(new_format: String ) {
            field = new_format
            formatter = NumberFormat(label_format)
        }

    private lateinit var formatter : NumberFormat

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        for (p in aesthetics.dataPoints()) {
            val x = p.x()
            val y = p.y()
            val text = toString(p.label())
            if (SeriesUtil.allFinite(x, y) && !Strings.isNullOrEmpty(text)) {
                val label = TextLabel(text)
                GeomHelper.decorate(label, p)

                val loc = helper.toClient(x, y, p)
                label.moveTo(loc)
                root.add(label.rootGroup)
            }
        }
    }

    fun toString( label: Any ) : String {
        if ( label is String )
            return label

        if ( label is Double ) {
            return formatter.apply(label)
        }

        error("Unacceptable label type.")
    }

    companion object {
        val DEF_LABEL_FORMAT = ".2f"
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.SIZE,
//                Aes.COLOR,
//                Aes.ALPHA,
//
//                Aes.LABEL,
//                Aes.FAMILY,
//                Aes.FONTFACE,
//                Aes.HJUST,
//                Aes.VJUST,
//                Aes.ANGLE
//        )

        val HANDLES_GROUPS = false
    }
}// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot
// ToDo: lineheight (aes)
// ToDo: nudge_x, nudge_y

