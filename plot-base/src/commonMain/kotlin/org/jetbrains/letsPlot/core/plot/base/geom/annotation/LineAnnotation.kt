/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.geom.util.PathData
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel


object LineAnnotation {

    private val startOffset = 90.0 // ToDo  Move to ctx.annotation / layer_labels()

    fun build(
        root: SvgRoot,
        pathData: List<PathData>,
        pathIds: List<String>?,
        coord: CoordinateSystem,
        ctx: GeomContext,
    ) {
        if (ctx.annotation == null) return
        
        val annotation = ctx.annotation!!

        if (pathIds != null) {
            createCurvedText(root, pathData.zip(pathIds), annotation, ctx)
        } else {
            createLabels(root, pathData, annotation, ctx, boundsCenter = coord.toClient(ctx.getAesBounds())?.center)
        }
    }


    private fun createCurvedText(
        root: SvgRoot,
        pathDataWithIds: List<Pair<PathData, String>>,
        annotation: Annotation,
        ctx: GeomContext,
    ) {
        pathDataWithIds
            .map { (path, id) ->
                val text = annotation.getAnnotationText(path.aes.index(), ctx.plotContext)
                val textSize = AnnotationUtil.textSizeGetter(annotation.textStyle, ctx).invoke(text, path.aes)
                val label = MultilineLabel.createCurvedLabel(
                    text,
                    textSize.y,
                    id,
                    startOffset
                )
                TextUtil.decorate(
                    label,
                    AnnotationUtil.toTextDataPointAesthetics(
                        AnnotationUtil.TextParams(
                            style = annotation.textStyle,
                            color = path.aes.color(),
                            fill = ctx.backgroundColor
                        )
                    )
                )
                root.add(label.rootGroup)
            }
    }

    private fun createLabels(
        root: SvgRoot,
        pathData: List<PathData>,
        annotation: Annotation,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ) {
        pathData
            .map { path ->
                val text = annotation.getAnnotationText(path.aes.index(), ctx.plotContext)

                val location = when {
                    startOffset == 50.0 -> {
                        val mid = path.coordinates.size / 2
                        path.coordinates[mid]
                    }

                    startOffset < 50 -> {
                        path.coordinates.first()
                    }

                    else -> {
                        path.coordinates.last()
                    }
                }

                val label = AnnotationUtil.createLabelElement(
                    text,
                    location,
                    textParams = AnnotationUtil.TextParams(
                        style = annotation.textStyle,
                        color = path.aes.color(),
                        hjust = "inward",
                        vjust = "bottom",
                        fill = ctx.backgroundColor,
                        alpha = 0.5
                    ),
                    geomContext = ctx,
                    boundsCenter = boundsCenter
                )
                root.add(label)
            }
    }
}