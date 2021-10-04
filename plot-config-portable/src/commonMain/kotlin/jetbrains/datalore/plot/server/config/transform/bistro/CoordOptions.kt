/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.config.Option


class CoordOptions internal constructor(
    val name: String?,
    val xLim: Pair<Double, Double>? = null,
    val yLim: Pair<Double, Double>? = null,
    val ratio: Double? = null
){
    companion object {
        fun coordCartesian(xLim: Pair<Double, Double>? = null, yLim: Pair<Double, Double>? = null): CoordOptions {
            return CoordOptions(Option.CoordName.CARTESIAN, xLim, yLim)
        }

        fun coordFixed(ratio: Double? = null, xLim: Pair<Double, Double>? = null, yLim: Pair<Double, Double>? = null): CoordOptions {
            return CoordOptions(Option.CoordName.FIXED, xLim, yLim, ratio)
        }
    }
}
