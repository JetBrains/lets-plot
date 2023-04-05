/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintsCollection
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class ErrorBarHGeom : ErrorBarGeom() {

    override fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.Y,
            Aes.XMIN,
            Aes.XMAX,
            Aes.HEIGHT
        )
    }

    override fun errorBarRectangle(p: DataPointAesthetics, ctx: GeomContext): DoubleRectangle {
        val y = p.y()!!
        val xmin = p.xmin()!!
        val xmax = p.xmax()!!
        val height = p.height()!! * ctx.getResolution(Aes.Y)
        val width = xmax - xmin
        return DoubleRectangle(xmin, y - height / 2, width, height)
    }

    override fun errorBarShapeSegments(r: DoubleRectangle): List<DoubleSegment> {
        val left = r.left
        val top = r.top
        val right = r.right
        val bottom = r.bottom
        val center = top + r.height / 2

        return listOf(
            DoubleSegment(DoubleVector(left, top), DoubleVector(left, bottom)),
            DoubleSegment(DoubleVector(right, top), DoubleVector(right, bottom)),
            DoubleSegment(DoubleVector(left, center), DoubleVector(right, center))
        )
    }

    override fun buildHints(
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>,
        root: SvgRoot
    ) {
        val rect = errorBarRectangle(p, ctx)

        val clientRect = geomHelper.toClient(rect, p)
        val objectRadius = clientRect?.run {
            if (ctx.flipped) {
                width / 2.0
            } else {
                height / 2.0
            }
        }!!

        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p.y()!!)
            .defaultKind(
                if (ctx.flipped) {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP  // or TipLayoutHint.Kind.ROTATED_TOOLTIP
                }
            )

        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(Aes.XMAX))
            .addHint(hint.create(Aes.XMIN))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p)
            ),
            tooltipKind = if (ctx.flipped) {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            }
        )
    }

    companion object {
        const val HANDLES_GROUPS = ErrorBarGeom.HANDLES_GROUPS
    }
}
