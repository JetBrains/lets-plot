/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr

import javafx.scene.shape.Rectangle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal object SvgRectAttrMapping : SvgShapeMapping<Rectangle>() {
    override fun setAttribute(target: Rectangle, name: String, value: Any?) {
        when (name) {
            SvgRectElement.X.name -> target.x = asDouble(value)
            SvgRectElement.Y.name -> target.y = asDouble(value)
            SvgRectElement.WIDTH.name -> {
                if (!ignoredSizeValue(value)) {
                    target.width = asDouble(value)
                }
            }
            SvgRectElement.HEIGHT.name -> {
                if (!ignoredSizeValue(value)) {
                    target.height = asDouble(value)
                }
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    private fun ignoredSizeValue(value: Any?): Boolean {
        // Do not fail on persentages, just ignore.
        return value is String && value.endsWith("%")
    }
}