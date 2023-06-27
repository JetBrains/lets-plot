/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx.attr

import javafx.scene.shape.Line
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import jetbrains.datalore.vis.svgMapper.jfx.unScale

internal object SvgLineAttrMapping : SvgShapeMapping<Line>() {
    override fun setAttribute(target: Line, name: String, value: Any?) {
        when (name) {
            SvgLineElement.X1.name -> target.startX = unScale(asDouble(value))
            SvgLineElement.Y1.name -> target.startY = unScale(asDouble(value))
            SvgLineElement.X2.name -> target.endX = unScale(asDouble(value))
            SvgLineElement.Y2.name -> target.endY = unScale(asDouble(value))
            else -> super.setAttribute(target, name, value)
        }
    }
}