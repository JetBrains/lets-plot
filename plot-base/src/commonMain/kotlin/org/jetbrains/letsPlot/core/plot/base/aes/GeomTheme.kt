/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color

interface GeomTheme {
    fun color(): Color

    fun fill(): Color

    fun alpha(): Double

    fun size(): Double

    fun lineWidth(): Double

    fun pen(): Color
}