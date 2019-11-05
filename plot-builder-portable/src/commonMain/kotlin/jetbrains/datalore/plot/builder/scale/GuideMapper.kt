/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.function.Function

interface GuideMapper<T> : Function<Double?, T?> {
    /**
     * @return TRUE if both, domain and range are continuous
     */
    val isContinuous: Boolean
}
