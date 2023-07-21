/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity

class MicroThreadComponent(
    val microTask: MicroTask<Unit>,
    internal val resumesBeforeTimeCheck: Int
) : EcsComponent

fun EcsEntity.setMicroThread(i: Int, f: MicroTask<Unit>) {
    this.setComponent(MicroThreadComponent(f, i))
}