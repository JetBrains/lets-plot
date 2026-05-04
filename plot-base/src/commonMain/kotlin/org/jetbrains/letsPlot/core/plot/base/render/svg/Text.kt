/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

object Text {

    enum class HorizontalAnchor {
        LEFT, RIGHT, MIDDLE
    }

    enum class VerticalAnchor {
        TOP, BOTTOM, CENTER
    }

    fun HorizontalAnchor.toDouble() = when (this) {
        HorizontalAnchor.LEFT -> 0.0
        HorizontalAnchor.MIDDLE -> 0.5
        HorizontalAnchor.RIGHT -> 1.0
    }

    fun VerticalAnchor.toDouble() = when (this) {
        VerticalAnchor.TOP -> 0.0
        VerticalAnchor.CENTER -> 0.5
        VerticalAnchor.BOTTOM -> 1.0
    }

    internal fun toTextAnchor(anchor: HorizontalAnchor): String? {
        return when (anchor) {
            HorizontalAnchor.LEFT -> null // default - "start";
            HorizontalAnchor.MIDDLE -> SvgConstants.SVG_TEXT_ANCHOR_MIDDLE
            HorizontalAnchor.RIGHT -> SvgConstants.SVG_TEXT_ANCHOR_END
        }
    }

    internal fun toDominantBaseline(anchor: VerticalAnchor): String? {
        return when (anchor) {
            VerticalAnchor.TOP -> "hanging"
            VerticalAnchor.CENTER -> "central"
            VerticalAnchor.BOTTOM -> null // default - "alphabetic";
        }
    }

    internal fun toDY(anchor: VerticalAnchor): String? {
        return when (anchor) {
            VerticalAnchor.TOP -> SvgConstants.SVG_TEXT_DY_TOP
            VerticalAnchor.CENTER -> SvgConstants.SVG_TEXT_DY_CENTER
            VerticalAnchor.BOTTOM -> null // default
        }
    }

    internal fun buildStyle(
        textColor: Color? = null,
        fontSize: Double? = null,
        fontWeight: String? = null,
        fontFamily: String? = null,
        fontStyle: String? = null
    ): String {
        val sb = StringBuilder()
        if (textColor != null) {
            sb.append(SvgUtils.fillAndOpacityStyle(textColor))
        }

        // set each property separately
        if (!fontStyle.isNullOrBlank()) {
            sb.append("font-style:$fontStyle;")
        }
        if (!fontWeight.isNullOrEmpty()) {
            sb.append("font-weight:$fontWeight;")
        }
        if (fontSize != null && fontSize > 0) {
            sb.append("font-size:${fontSize}px;")
        }
        if (!fontFamily.isNullOrEmpty()) {
            sb.append("font-family:$fontFamily;")
        }
        return sb.toString()
    }
}
