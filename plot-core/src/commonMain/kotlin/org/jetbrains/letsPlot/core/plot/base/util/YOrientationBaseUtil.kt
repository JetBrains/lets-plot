/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.util

import org.jetbrains.letsPlot.core.plot.base.Aes

fun <T> org.jetbrains.letsPlot.core.plot.base.Aes<T>.afterOrientation(yOrientation: Boolean): org.jetbrains.letsPlot.core.plot.base.Aes<T> {
    return when (yOrientation) {
        true -> YOrientationBaseUtil.flipAes(this)
        false -> this
    }
}

fun List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>.afterOrientation(yOrientation: Boolean): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
    return this.map { it.afterOrientation(yOrientation) }
}

fun Set<org.jetbrains.letsPlot.core.plot.base.Aes<*>>.afterOrientation(yOrientation: Boolean): Set<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
    return this.map { it.afterOrientation(yOrientation) }.toSet()
}


object YOrientationBaseUtil {
    fun <T> flipAesKeys(map: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, T>): Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, T> {
        return map.mapKeys { (aes, _) ->
            flipAes(aes)
        }
    }

    fun <T> flipAes(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): org.jetbrains.letsPlot.core.plot.base.Aes<T> {
        @Suppress("UNCHECKED_CAST")
        return when (aes) {
            org.jetbrains.letsPlot.core.plot.base.Aes.X -> org.jetbrains.letsPlot.core.plot.base.Aes.Y as org.jetbrains.letsPlot.core.plot.base.Aes<T>
            org.jetbrains.letsPlot.core.plot.base.Aes.Y -> org.jetbrains.letsPlot.core.plot.base.Aes.X as org.jetbrains.letsPlot.core.plot.base.Aes<T>
            else -> aes
        }
    }
}