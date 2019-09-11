package jetbrains.livemap.camera

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations.EASE_OUT_QUAD
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.WorldPoint

object CameraScale {

    fun setAnimation(cameraEntity: EcsEntity, origin: ClientPoint, center: WorldPoint, delta: Double) {
        val camera = cameraEntity.get<CameraComponent>()

        if (camera.zoom % 1 != 0.0) {
            error("Non integer camera zoom detected: ${camera.zoom}")
        }

        val animation = cameraEntity.componentManager
            .createEntity("camera_scale_animation")
            .addComponent(
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
                    origin,
                    center,
                    delta,
                    camera.zoom
                )
            )
    }

    class CameraScaleEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<LiveMapContext>(componentManager) {

        override fun updateImpl(context: LiveMapContext, dt: Double) {
            val cameraEntity = getSingletonEntity(CameraComponent::class)
            val camera = cameraEntity.get<CameraComponent>()

            cameraEntity.tryGet<CameraScaleEffectComponent>()?.let { scaleEffect ->
                val animation = getEntityById(scaleEffect.animationId) ?: return

                val progress = AnimationComponent[animation].progress
                val deltaZoom = scaleEffect.delta * progress

                scaleEffect.currentScale = if (deltaZoom < 0) 0.5 + (1 + deltaZoom) / 2 else 1.0 + deltaZoom

                camera.zoom = scaleEffect.startZoom + deltaZoom

                if (progress == 1.0) {
                    camera.center = scaleEffect.newCenter
                    cameraEntity.removeComponent(CameraScaleEffectComponent::class)
                    cameraEntity.tag(::UpdateViewProjectionComponent)
                }
            }
        }
    }

    class CameraScaleEffectComponent(
        val animationId: Int,
        val scaleOrigin: ClientPoint,
        val newCenter: WorldPoint,
        val delta: Double,
        val startZoom: Double
    ) : EcsComponent {
        var currentScale: Double = 0.0
    }
}