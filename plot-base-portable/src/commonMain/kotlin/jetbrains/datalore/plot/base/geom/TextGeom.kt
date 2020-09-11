/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.common.data.SeriesUtil


class TextGeom : GeomBase() {
    var formatter: StringFormat? = null
    var naValue = DEF_NA_VALUE
    var sizeUnit: String? = null
    var sizeUnitScale: Double? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory()

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)
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

                targetCollector.addPoint(
                    p.index(),
                    loc,
                    AesScaling.textSize(p) / 2,
                    GeomTargetCollector.TooltipParams.params()
                        .setColor(HintColorUtil.fromColor(p))
                        .setStemLength(TipLayoutHint.StemLength.NONE)
                )
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
        return when {
            label == null -> naValue
            formatter != null -> formatter!!.format(label)
            else -> label.toString()
        }
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

