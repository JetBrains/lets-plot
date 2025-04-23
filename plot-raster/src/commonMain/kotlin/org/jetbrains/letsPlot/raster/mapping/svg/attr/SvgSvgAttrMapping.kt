/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.shape.Pane

internal object SvgSvgAttrMapping : SvgAttrMapping<Pane>() {

    override fun setAttribute(target: Pane, name: String, value: Any?) {
        when (name) {
            SvgSvgElement.X.name -> target.transform = target.transform.withTx(value?.asFloat ?: 0.0f)
            SvgSvgElement.Y.name -> target.transform = target.transform.withTy(value?.asFloat ?: 0.0f)
            SvgSvgElement.WIDTH.name -> target.width = value?.asFloat ?: 0.0f
            SvgSvgElement.HEIGHT.name -> target.height = value?.asFloat ?: 0.0f
            "display" /*SvgSvgElement.DISPLAY.name*/ -> { }  // ignore
            else -> super.setAttribute(target, name, value)
        }
    }
}

private fun AffineTransform.withTy(f: Float): AffineTransform {
    return AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = f.toDouble())
}

private fun AffineTransform.withTx(f: Float): AffineTransform {
    return AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = f.toDouble(), ty = ty)
}
