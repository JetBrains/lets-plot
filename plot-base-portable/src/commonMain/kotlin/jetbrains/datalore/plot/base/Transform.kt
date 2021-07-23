/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

interface Transform {
    fun apply(l: List<*>): List<Double?>

    fun applyInverse(v: Double?): Any?
}
