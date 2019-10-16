package jetbrains.livemap.ui

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.rendering.primitives.RenderBox

class UiRenderComponent(internal val renderBox: RenderBox) : EcsComponent
fun uiRenderer(renderBox: RenderBox) = UiRenderComponent(renderBox)
