/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.ui

import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.graphics.RenderBox

class UiRenderComponent(
    internal val renderBox: RenderBox
) : EcsComponent {
    val origin get() = renderBox.origin
    val dimension get() = renderBox.dimension

}
