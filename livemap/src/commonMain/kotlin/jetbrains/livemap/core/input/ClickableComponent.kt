/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox

class ClickableComponent(rect: RenderBox) : EcsComponent {
    val rect = DoubleRectangle(rect.origin, rect.dimension)
}
