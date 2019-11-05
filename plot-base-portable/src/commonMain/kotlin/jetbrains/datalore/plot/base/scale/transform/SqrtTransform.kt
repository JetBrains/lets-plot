/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import kotlin.math.sqrt

class SqrtTransform : FunTransform(
    F,
    F_INVERSE
) {
    companion object {
        private val F: (Double?) -> Double? = { v ->
            if (v != null)
                sqrt(v)
            else
                null
        }
        private val F_INVERSE: (Double?) -> Double? = { v ->
            if (v != null)
                v * v
            else
                null
        }
    }
}