/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object PlotSvgHelper {
    fun fetchPlotSizeFromSvg(svg: String): DoubleVector {
        val svgTagMatch = Regex("<svg (.*)>").find(svg)
        require(svgTagMatch != null && svgTagMatch.groupValues.size == 2) {
            "Couldn't find 'svg' tag"
        }

        val svgTag = svgTagMatch.groupValues[1]

        val width = extractDouble(Regex(".*width=\"(\\d+)\\.?(\\d+)?\""), svgTag)
        val height = extractDouble(Regex(".*height=\"(\\d+)\\.?(\\d+)?\""), svgTag)
        return DoubleVector(width, height)
    }

    private fun extractDouble(regex: Regex, text: String): Double {
        val matchResult = regex.find(text)!!
        val values = matchResult.groupValues
        return if (values.size < 3)
            "${values[1]}".toDouble()
        else
            "${values[1]}.${values[2]}".toDouble()
    }
}