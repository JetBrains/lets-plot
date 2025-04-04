/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.util

import org.jetbrains.letsPlot.core.plot.base.Aes

fun <T> Aes<T>.afterOrientation(yOrientation: Boolean): Aes<T> {
    return when (yOrientation) {
        true -> YOrientationBaseUtil.flipAes(this)
        false -> this
    }
}

fun List<Aes<*>>.afterOrientation(yOrientation: Boolean): List<Aes<*>> {
    return this.map { it.afterOrientation(yOrientation) }
}

fun Set<Aes<*>>.afterOrientation(yOrientation: Boolean): Set<Aes<*>> {
    return this.map { it.afterOrientation(yOrientation) }.toSet()
}


object YOrientationBaseUtil {
    fun <T> flipAesKeys(map: Map<Aes<*>, T>): Map<Aes<*>, T> {
        return map.mapKeys { (aes, _) ->
            flipAes(aes)
        }
    }

    fun <T> flipAes(aes: Aes<T>): Aes<T> {
        @Suppress("UNCHECKED_CAST")
        return when (aes) {
            Aes.X -> Aes.Y as Aes<T>
            Aes.Y -> Aes.X as Aes<T>
            Aes.YMIN -> Aes.XMIN as Aes<T>
            Aes.YMAX -> Aes.XMAX as Aes<T>
            Aes.XMIN -> Aes.YMIN as Aes<T>
            Aes.XMAX -> Aes.YMAX as Aes<T>
            Aes.LOWER -> Aes.XLOWER as Aes<T>
            Aes.UPPER -> Aes.XUPPER as Aes<T>
            Aes.XLOWER -> Aes.LOWER as Aes<T>
            Aes.XUPPER -> Aes.UPPER as Aes<T>
            Aes.MIDDLE -> Aes.XMIDDLE as Aes<T>
            Aes.XMIDDLE -> Aes.MIDDLE as Aes<T>
            else -> aes
        }
    }
}