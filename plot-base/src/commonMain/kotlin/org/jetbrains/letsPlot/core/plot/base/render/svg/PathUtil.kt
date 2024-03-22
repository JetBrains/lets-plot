/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder

fun SvgPathDataBuilder.lineString(points: List<DoubleVector>): SvgPathDataBuilder {
    if (points.isEmpty()) return this

    moveTo(points.first())
    points.asSequence().drop(1).forEach(::lineTo)
    return this
}
