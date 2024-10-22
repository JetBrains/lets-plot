/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.mapConfig

import org.jetbrains.letsPlot.commons.values.Color

class Style {
    var type: String? = null
    var fill: Color? = null
    var stroke: Color? = null
    var strokeWidth: Double? = null
    var lineCap: String? = null
    var lineJoin: String? = null
    var lineDash: List<Double>? = null
    var lineDashOffset: Double? = null
    var labelField: String? = null
    var fontStyle: String? = null
    var fontFamily: String? = null
    var textTransform: String? = null
    var size: Double? = null
    var wrapWidth: Double? = null
    var minimumPadding: Double? = null
    var repeatDistance: Double? = null
    var shieldCornerRadius: Double? = null
    var shieldFillColor: Color? = null
    var shieldStrokeColor: Color? = null
}