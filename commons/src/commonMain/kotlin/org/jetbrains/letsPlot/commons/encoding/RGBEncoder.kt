/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

interface RGBEncoder {
    fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String
}