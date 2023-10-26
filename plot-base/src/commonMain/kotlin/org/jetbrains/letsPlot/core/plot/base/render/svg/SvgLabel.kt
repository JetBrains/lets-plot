/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.richText.RichText

abstract class SvgLabel : SvgComponent() {
    abstract fun textColor(): WritableProperty<Color?>

    abstract fun setHorizontalAnchor(anchor: Text.HorizontalAnchor)

    abstract fun setFontSize(px: Double)

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    abstract fun setFontWeight(cssName: String?)

    /**
     * @param cssName : normal, italic, oblique
     */
    abstract fun setFontStyle(cssName: String?)

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    abstract fun setFontFamily(fontFamily: String?)

    protected fun enrichText(textElement: SvgTextElement): SvgTextElement {
        // Process only the case when originally it was a plain text
        if (textElement.children().size != 1) {
            return textElement
        }
        return RichText.enrichText(textElement)
    }
}