/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro.util

import jetbrains.datalore.plot.config.Option

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
