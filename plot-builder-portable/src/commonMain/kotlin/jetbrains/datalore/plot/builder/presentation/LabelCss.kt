/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

object LabelCss {
    operator fun get(labelSpec: LabelSpec, selector: String): String {
        val css = StringBuilder()
        css
            .append(selector).append(" {")
            .append(
                if (labelSpec.isMonospaced)
                    "\n  font-family: " + Defaults.FONT_FAMILY_MONOSPACED + ";"
                else
                    "\n"
            )
            .append("\n  font-size: ").append(labelSpec.fontSize).append("px;")
            .append(if (labelSpec.isBold) "\n  font-weight: bold;" else "")
            //      .append("\n  fill: red;")
            .append("\n}\n")
        return css.toString()
    }
}
