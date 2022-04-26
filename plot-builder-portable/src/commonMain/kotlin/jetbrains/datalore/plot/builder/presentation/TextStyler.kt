/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.FontProperties
import jetbrains.datalore.vis.StyleRenderer


open class TextStyler(private val myTextStyles: Map<String, FontProperties>) : StyleRenderer {

    override fun has(className: String): Boolean {
        return myTextStyles.containsKey(className)
    }

    override fun getColor(className: String): Color {
        return myTextStyles[className]?.color ?: UNSUPPORTED("Unknown class name: $className.")
    }

    override fun getFontSize(className: String): Double {
        return myTextStyles[className]?.size ?: UNSUPPORTED("Unknown class name: $className.")
    }

    override fun getFontFamily(className: String): String {
        return myTextStyles[className]?.family?.toString() ?: UNSUPPORTED("Unknown class name: $className.")
    }

    override fun getIsItalic(className: String): Boolean {
        val face = myTextStyles[className]?.face ?: UNSUPPORTED("Unknown class name: $className.")
        return face.italic
    }

    override fun getIsBold(className: String): Boolean {
        val face = myTextStyles[className]?.face ?: UNSUPPORTED("Unknown class name: $className.")
        return face.bold
    }

    fun css(): String {
        val css = StringBuilder()
        myTextStyles.forEach { (className, props) ->
            css
                .append(".$className text").append(" {")
                .append("\n  fill: ").append(props.color.toHexColor() + ";")
                .append("\n  font-family: ").append(props.family.toString() + ";")
                .append("\n  font-size: ").append(props.size).append("px;")
                .append("\n  font-weight: ").append(if (props.face.bold) "bold;" else "normal;")
                .append("\n  font-style: ").append(if (props.face.italic) "italic;" else "normal;")
                .append("\n}\n")
        }
        return css.toString()
    }
}