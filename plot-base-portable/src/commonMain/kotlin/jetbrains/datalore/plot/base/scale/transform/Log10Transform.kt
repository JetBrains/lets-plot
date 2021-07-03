/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import kotlin.math.log10
import kotlin.math.pow

internal class Log10Transform : FunTransform(F, F_INVERSE) {
    companion object {
        val F: (Double?) -> Double? = { v ->
            if (v != null)
                log10(v).takeIf { !it.isNaN() }
            else
                null
        }
        val F_INVERSE: (Double?) -> Double? = { v ->
            if (v != null)
                10.0.pow(v)
            else
                null
        }
    }
}
