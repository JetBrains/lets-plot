/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange

interface BreaksGenerator {
    fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks
    fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String
}
