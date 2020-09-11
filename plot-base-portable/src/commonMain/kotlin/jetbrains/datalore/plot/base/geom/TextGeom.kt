/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement

class TextGeom : GeomBase() {
    var formatter: NumberFormat? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var sizeUnitScale: Double? = null

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
                val scale = getScale(ctx, p)
                GeomHelper.decorate(label, p, scale )

                val loc = helper.toClient(x, y, p)
                label.moveTo(loc)
                root.add(label.rootGroup)
            }
        }
    }

    private fun getScale(ctx: GeomContext, p: DataPointAesthetics) : Double {
        sizeUnitScale?.let { return sizeUnitScale!! }
        sizeUnitScale = 1.0

        sizeUnit?.let {
            val aes = GeomHelper.getSizeUnitAes(sizeUnit!!)
            val testString = toString(testValue)
            val label = TextLabel(testString)
            GeomHelper.decorate(label, p, 1.0 )
            val unitRes = ctx.getUnitResolution(aes)
//            val textWidth = label.computedTextLength
            val textWidth = 50.0

            sizeUnitScale = unitRes / textWidth
        }

        return sizeUnitScale!!
    }

    private fun toString(label: Any?): String {
        if (label == null) {
            return naValue
        }

        if (label is Double) {
            formatter?.let { return it.apply(label) }
        }

        return label.toString()
    }

    companion object {
        val DEF_NA_VALUE = "n/a"
        val HANDLES_GROUPS = false
        const val testValue = -9.99999
    }
}

// How 'just' and 'angle' works together
// https://stackoverflow.com/questions/7263849/what-do-hjust-and-vjust-do-when-making-a-plot-using-ggplot
// ToDo: lineheight (aes)
// ToDo: nudge_x, nudge_y

