/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.config.Option


class CoordOptions : Options() {
    var name: String? by map(Option.Meta.NAME)
    var xLim: Pair<Double, Double>? by map(Option.Coord.X_LIM)
    var yLim: Pair<Double, Double>? by map(Option.Coord.Y_LIM)
    var ratio: Double? by map(Option.Coord.RATIO)
}

fun coord(block: CoordOptions.() -> Unit) = CoordOptions().apply(block)
