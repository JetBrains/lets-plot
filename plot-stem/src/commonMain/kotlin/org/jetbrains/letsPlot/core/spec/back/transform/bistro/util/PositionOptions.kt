/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.util

import org.jetbrains.letsPlot.core.spec.Option

class PositionOptions : Options() {
    var name: String? by map(Option.Pos.NAME)
    var x: Double? by map(Option.Pos.Nudge.WIDTH)
    var y: Double? by map(Option.Pos.Nudge.HEIGHT)
}

fun position(block: PositionOptions.() -> Unit) = PositionOptions().apply(block)