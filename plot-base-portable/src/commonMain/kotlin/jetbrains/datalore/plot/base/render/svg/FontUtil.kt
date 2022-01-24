/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.values.Color

object FontUtil {

    internal fun buildStyleAttribute(
        textColor: Color?,
        fontSize: Double,
        fontWeight: String?,
        fontFamily: String?,
        fontStyle: String?
    ): String {
        val sb = StringBuilder()
        if (textColor != null) {
            sb.append("fill:").append(textColor.toHexColor()).append(';')
        }

        if (fontSize > 0 && fontFamily != null) {
            // use font shorthand because this format is expected by svg -> canvas mapper
            // font: [style] [weight] size family;
            val fnt = StringBuilder()
            if (!fontStyle.isNullOrEmpty()) {
                fnt.append(fontStyle).append(' ')
            }
            if (!fontWeight.isNullOrEmpty()) {
                fnt.append(fontWeight).append(' ')
            }
            fnt.append(fontSize).append("px ")
            fnt.append(fontFamily).append(";")

            sb.append("font:").append(fnt)
        } else {
            // set each property separately
            if (!fontStyle.isNullOrBlank()) {
                sb.append("font-style:").append(fontStyle).append(';')
            }
            if (!fontWeight.isNullOrEmpty()) {
                sb.append("font-weight:").append(fontWeight).append(';')
            }
            if (fontSize > 0) {
                sb.append("font-size:").append(fontSize).append("px;")
            }
            if (!fontFamily.isNullOrEmpty()) {
                sb.append("font-family:").append(fontFamily).append(';')
            }
        }

        return sb.toString()
    }
}