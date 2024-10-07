/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.PosProto

class PositionOptions : Options() {
    var name: PosKind? by map(Option.Pos.NAME)
    var x: Double? by map(Option.Pos.Nudge.WIDTH)
    var y: Double? by map(Option.Pos.Nudge.HEIGHT)

    enum class PosKind(
        val value: String
    ) {
        DODGE(PosProto.DODGE),
        DODGE_V(PosProto.DODGE_V),
        JITTER(PosProto.JITTER),
        STACK(PosProto.STACK),
        IDENTITY(PosProto.IDENTITY),
        FILL(PosProto.FILL),
        NUDGE(PosProto.NUDGE),
        JITTER_DODGE(PosProto.JITTER_DODGE),
    }
}

fun identity(block: PositionOptions.() -> Unit = {}) = PositionOptions().apply {
    name = PositionOptions.PosKind.IDENTITY
    block()
}

fun dodge(block: PositionOptions.() -> Unit = {}) = PositionOptions().apply {
    name = PositionOptions.PosKind.DODGE
    block()
}

fun stack(block: PositionOptions.() -> Unit = {}) = PositionOptions().apply {
    name = PositionOptions.PosKind.STACK
    block()
}

fun fill(block: PositionOptions.() -> Unit = {}) = PositionOptions().apply {
    name = PositionOptions.PosKind.FILL
    block()
}

fun nudge(block: PositionOptions.() -> Unit = {}) = PositionOptions().apply {
    name = PositionOptions.PosKind.NUDGE
    block()
}
