/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface Projection {
    val nonlinear: Boolean

    fun apply(v: Double): Double

    fun toValidDomain(domain: ClosedRange<Double>): ClosedRange<Double>
}
