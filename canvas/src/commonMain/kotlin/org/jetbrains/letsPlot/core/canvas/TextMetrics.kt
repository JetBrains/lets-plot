/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle

data class TextMetrics(
    val ascent: Double,
    val descent: Double,
    val bbox: DoubleRectangle,
)

