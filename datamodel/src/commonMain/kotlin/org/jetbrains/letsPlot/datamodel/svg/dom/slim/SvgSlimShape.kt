/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

import org.jetbrains.letsPlot.commons.values.Color

interface SvgSlimShape : SvgSlimObject {
    fun setFill(c: Color, alpha: Double)
    fun setStroke(c: Color, alpha: Double)
    fun setStrokeWidth(v: Double)
    fun setStrokeDashArray(v: String)
}
