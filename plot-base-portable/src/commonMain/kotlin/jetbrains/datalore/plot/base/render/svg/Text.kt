/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgConstants

object Text {

    enum class HorizontalAnchor {
        LEFT, RIGHT, MIDDLE
    }

    enum class VerticalAnchor {
        TOP, BOTTOM, CENTER
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
            sb.append("fill:").append(textColor.toHexColor()).append(';')
        }

        // set each property separately
        if (!fontStyle.isNullOrBlank()) {
            sb.append("font-style:").append(fontStyle).append(';')
        }
        if (!fontWeight.isNullOrEmpty()) {
            sb.append("font-weight:").append(fontWeight).append(';')
        }
        if (fontSize != null && fontSize > 0) {
            sb.append("font-size:").append(fontSize).append("px;")
        }
        if (!fontFamily.isNullOrEmpty()) {
            sb.append("font-family:").append(fontFamily).append(';')
        }
        return sb.toString()
    }
}