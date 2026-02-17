/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.core.plot.base.geom.BlankGeom.Companion.LabelX
import org.jetbrains.letsPlot.core.plot.base.geom.BlankGeom.Companion.LabelY
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class SmoothAnnotation(
    lines: List<LineSpec>,
    textStyle: TextStyle,
    useCustomColor: Boolean,
    useLayerColor: Boolean,
    val labelX: List<Pair<Double?, LabelX>>,
    val labelY: List<Pair<Double?, LabelY>>
): Annotation(lines, textStyle, useCustomColor, useLayerColor)