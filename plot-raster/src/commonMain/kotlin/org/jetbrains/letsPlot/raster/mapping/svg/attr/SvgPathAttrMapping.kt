/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.raster.mapping.svg.SvgPathParser
import org.jetbrains.letsPlot.raster.shape.Path

internal object SvgPathAttrMapping : SvgShapeMapping<Path>() {
    override fun setAttribute(target: Path, name: String, value: Any?) {
        when (name) {
            SvgPathElement.STROKE_MITER_LIMIT.name -> target.strokeMiter = value?.asFloat

            SvgPathElement.FILL_RULE.name -> {
                target.fillRule = when (value) {
                    SvgPathElement.FillRule.NON_ZERO ->  Path.FillRule.NON_ZERO
                    SvgPathElement.FillRule.EVEN_ODD -> Path.FillRule.EVEN_ODD
                    null -> Path.FillRule.NON_ZERO
                    else -> throw IllegalArgumentException("Unknown fill-rule: $value")
                }
            }

            SvgPathElement.D.name -> {
                // Can be string (slim path) or SvgPathData
                val pathStr = when (value) {
                    is String -> value
                    is SvgPathData -> value.toString()
                    null -> throw IllegalArgumentException("Undefined `path data`")
                    else -> throw IllegalArgumentException("Unexpected `path data` type: ${value::class.simpleName}")
                }

                target.pathData = SvgPathParser.parse(pathStr)
            }
            else -> super.setAttribute(target, name, value)
        }
    }
}
