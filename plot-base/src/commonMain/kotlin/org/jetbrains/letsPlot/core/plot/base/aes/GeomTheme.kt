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

    fun pointSize(): Double

    companion object {
        val NONE = object : GeomTheme {
            override fun pen(): Color = Color.PACIFIC_BLUE
            override fun color(): Color = Color.PACIFIC_BLUE
            override fun fill(): Color = Color.PACIFIC_BLUE
            override fun alpha() = 1.0
            override fun size() = 1.0
            override fun lineWidth() = 1.0
            override fun pointSize() = 1.0
        }
    }
}