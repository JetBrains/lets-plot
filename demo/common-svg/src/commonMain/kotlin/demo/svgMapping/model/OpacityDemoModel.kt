/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.model

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

object OpacityDemoModel {
    fun createModel() = SvgSvgElement(width = 600.0, height = 400.0).apply {
        var i = 0
        fun SvgNode.row(text: String, opacity: Double? = null, strokeOpacity: Double? = null, fillOpacity: Double? = null) {
            val x = 200.0
            val size = 60.0
            val strokeWidth = 15.0
            val vMargin = 30.0
            val hMargin = 10.0
            val y = 40 + i * (size + vMargin)
            i++

            rect(x, y, size, size, SvgColors.CRIMSON, SvgColors.STEEL_BLUE, strokeWidth) {
                opacity?.let { setAttribute("opacity", it.toString()) }
                strokeOpacity?.let { setAttribute("stroke-opacity", it.toString()) }
                fillOpacity?.let { setAttribute("fill-opacity", it.toString()) }
            }
            text(text, x = x + size + hMargin, y = y + size / 2)
        }

        g {
            row("opacity=0.5", opacity = 0.5)
            row("fill/stroke-opacity=0.5", fillOpacity = 0.5, strokeOpacity = 0.5)
            row("fill-opacity=0.5", fillOpacity = 0.5)
            row("stroke-opacity=0.5", strokeOpacity = 0.5)
        }
    }
}