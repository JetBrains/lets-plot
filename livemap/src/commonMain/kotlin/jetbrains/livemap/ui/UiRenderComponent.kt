/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox

class UiRenderComponent(internal val renderBox: RenderBox) : EcsComponent
fun uiRenderer(renderBox: RenderBox) = UiRenderComponent(renderBox)
