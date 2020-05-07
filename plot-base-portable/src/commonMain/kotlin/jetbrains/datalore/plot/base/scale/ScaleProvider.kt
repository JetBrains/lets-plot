/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale

interface ScaleProvider<T> {
    fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale<T>
}
