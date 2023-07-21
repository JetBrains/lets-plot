/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.util

import kotlin.math.pow

typealias EasingFunction = (Double) -> Double

object EasingFunctions {
    val LINEAR: EasingFunction = { x -> x }
    val EASE_IN_QUAD: EasingFunction = { x -> x * x }
    val EASE_IN_QUBIC: EasingFunction = { x -> x * x * x }
    val EASE_IN_QUART: EasingFunction = { x -> x * x * x * x }
    val EASE_OUT_QUAD: EasingFunction = { x -> 1 - (1 - x) * (1 - x) }
    val EASE_OUT_QUBIC: EasingFunction = { x -> 1 - (1 - x).pow(3) }
    val EASE_OUT_QUART: EasingFunction = { x -> 1 - (1 - x).pow(4) }
}
