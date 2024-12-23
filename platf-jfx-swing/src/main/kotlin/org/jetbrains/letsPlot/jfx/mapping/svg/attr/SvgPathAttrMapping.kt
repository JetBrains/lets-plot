/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg.attr

import javafx.scene.shape.SVGPath
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement

internal object SvgPathAttrMapping : SvgShapeMapping<SVGPath>() {
    override fun setAttribute(target: SVGPath, name: String, value: Any?) {
        when (name) {
            SvgPathElement.FILL_RULE.name -> {
                val fillRule = when (value as? SvgPathElement.FillRule) {
                    SvgPathElement.FillRule.EVEN_ODD -> javafx.scene.shape.FillRule.EVEN_ODD
                    SvgPathElement.FillRule.NON_ZERO -> javafx.scene.shape.FillRule.NON_ZERO
                    null -> null
                }
                target.fillRule = fillRule
            }

            SvgPathElement.D.name -> {
                // Can be string (slim path) or SvgPathData
                val pathStr = when (value) {
                    is String -> value
                    is SvgPathData -> value.toString()
                    null -> throw IllegalArgumentException("Undefined `path data`")
                    else -> throw IllegalArgumentException("Unexpected `path data` type: ${value::class.simpleName}")
                }

                target.content = pathStr
            }

            SvgPathElement.STROKE_MITER_LIMIT.name -> {
                target.strokeMiterLimit = asDouble(value)
            }

            else -> super.setAttribute(target, name, value)
        }
    }
}