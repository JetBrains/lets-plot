/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.config.Option

class SamplingOptions(
    val kind: String,
    val n: Int? = null,
    val seed: Int? = null,
    val minSubsample: Int? = null
) {
    companion object {
        val NONE = SamplingOptions(Option.Sampling.NONE)
    }
}
