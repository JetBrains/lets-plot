/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations.EASE_OUT_QUAD
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.WorldPoint
import kotlin.math.sign

object CameraScale {

    fun setAnimation(cameraEntity: EcsEntity, scaleOrigin: ClientPoint, viewportPosition: WorldPoint, delta: Double) {
        val camera = cameraEntity.get<CameraComponent>()

        if (camera.zoom % 1 != 0.0) {
            error("Non integer camera zoom detected: ${camera.zoom}")
        }

        val animation = cameraEntity.componentManager
            .createEntity("camera_scale_animation")
            .add(
                AnimationComponent().apply {
                    duration = 250.0
                    easingFunction = EASE_OUT_QUAD
                    loop = Animation.Loop.DISABLED
                    direction = Animation.Direction.FORWARD
                }
            )

        cameraEntity
            .setComponent(
                CameraScaleEffectComponent(
                    animation.id,
                    scaleOrigin,
                    viewportPosition,
                    delta,
                    camera.zoom
                )
            )
    }

    class CameraScaleEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<LiveMapContext>(componentManager) {

        override fun updateImpl(context: LiveMapContext, dt: Double) {
            val cameraEntity = getSingletonEntity(CameraComponent::class)

            cameraEntity.tryGet<CameraScaleEffectComponent>()?.let { scaleEffect ->
                val animation = getEntityById(scaleEffect.animationId) ?: return

                val progress = animation.get<AnimationComponent>().progress
                val deltaZoom = scaleEffect.delta * progress

                scaleEffect.currentScale = when(scaleEffect.delta.sign) {
                    -1.0 -> 1.0 + deltaZoom / 2
                    else -> 1.0 + deltaZoom
                }

                context.camera.requestZoom(scaleEffect.startZoom + deltaZoom)

                if (progress == 1.0) {
                    context.camera.requestPosition(scaleEffect.viewportPosition)
                    cameraEntity.remove<CameraScaleEffectComponent>()
                    // cameraEntity.tag(::UpdateViewportComponent)
                }
            }
        }
    }

    class CameraScaleEffectComponent(
        val animationId: Int,
        val scaleOrigin: ClientPoint,
        val viewportPosition: WorldPoint,
        val delta: Double,
        val startZoom: Double
    ) : EcsComponent {
        var currentScale: Double = 0.0
    }
}
