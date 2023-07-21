/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.util

import org.jetbrains.letsPlot.core.spec.Option

class SamplingOptions : Options() {
    var kind: String? by map(Option.Meta.NAME)
    var n: Int? by map(Option.Sampling.N)
    var seed: Int? by map(Option.Sampling.SEED)
    var minSubsample: Int?  by map(Option.Sampling.MIN_SUB_SAMPLE)

    companion object {
        val NONE = SamplingOptions().apply {
            kind = Option.Sampling.NONE
        }
    }
}

fun sampling(block: SamplingOptions.() -> Unit) = SamplingOptions().apply(block)
