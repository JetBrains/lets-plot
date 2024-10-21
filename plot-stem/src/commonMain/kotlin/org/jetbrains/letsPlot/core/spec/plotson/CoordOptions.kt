/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option


class CoordOptions : Options() {
    var name: CoordName? by map(Option.Meta.NAME)
    var xLim: Pair<Double?, Double?>? by map(Option.Coord.X_LIM)
    var yLim: Pair<Double?, Double?>? by map(Option.Coord.Y_LIM)
    var ratio: Double? by map(Option.Coord.RATIO)

    enum class CoordName(val value: String) {
        CARTESIAN(Option.CoordName.CARTESIAN),
        FIXED(Option.CoordName.FIXED),
        MAP(Option.CoordName.MAP),
        FLIP(Option.CoordName.FLIP),
        POLAR(Option.CoordName.POLAR),
    }
}

fun coord(block: CoordOptions.() -> Unit) = CoordOptions().apply(block)
