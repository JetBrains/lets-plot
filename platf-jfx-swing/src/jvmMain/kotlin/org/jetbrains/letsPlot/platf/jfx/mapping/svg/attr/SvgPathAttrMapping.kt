/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr

import javafx.scene.shape.SVGPath
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement

internal object SvgPathAttrMapping : SvgShapeMapping<SVGPath>() {
    override fun setAttribute(target: SVGPath, name: String, value: Any?) {
        when (name) {
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
            else -> super.setAttribute(target, name, value)
        }
    }
}