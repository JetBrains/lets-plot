/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile

class VisualAssertion {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer
    open val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Strict

}