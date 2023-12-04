/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks

internal class BogusScale : Scale {
    override val name: String
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val labelFormatter: ((Any) -> String)
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val isContinuous: Boolean
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val isContinuousDomain: Boolean
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val multiplicativeExpand: Double
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val additiveExpand: Double
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val transform: Transform
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override fun hasBreaks(): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun getScaleBreaks(): ScaleBreaks {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun getShortenedScaleBreaks(): ScaleBreaks {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun with(): Scale.Builder {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }
}
