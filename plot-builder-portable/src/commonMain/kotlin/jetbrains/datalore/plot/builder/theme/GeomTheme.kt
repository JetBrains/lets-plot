/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color

interface GeomTheme {
    fun color(): Color?

    fun fill(): Color?

    fun size(): Double?

    fun alpha(): Double?
}