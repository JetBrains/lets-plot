/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.interval.DoubleSpan

interface StatContext {
    fun overallXRange(): DoubleSpan?

    fun overallYRange(): DoubleSpan?
}
