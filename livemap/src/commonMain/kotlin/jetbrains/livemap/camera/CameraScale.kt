package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations.EASE_OUT_QUAD
import jetbrains.livemap.core.ecs.*

object CameraScale {

    fun setAnimation(cameraEntity: EcsEntity, origin: DoubleVector, center: DoubleVector, delta: Double) {
        val camera: CameraComponent = cameraEntity.getComponent()

        val manager = cameraEntity.componentManager

        if (camera.zoom % 1 != 0.0) {
            error("Non integer camera zoom detected: ${camera.zoom}")
        }

        val animation = manager
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
                CameraScaleEffectComponent().apply {
                    animationId = animation.id
                    scaleOrigin = origin
                    newCenter = center
                    startZoom = camera.zoom
                    this.delta = delta
                }
            )
    }

    class CameraScaleEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<LiveMapContext>(componentManager) {

        override fun updateImpl(context: LiveMapContext, dt: Double) {
            val cameraEntity = getSingletonEntity(CameraComponent::class)
            val camera: CameraComponent = cameraEntity.getComponent()

            if (cameraEntity.contains(CameraScaleEffectComponent::class)) {
                val scaleEffect = CameraScaleEffectComponent[cameraEntity]

                val animation = getEntityById(scaleEffect.animationId) ?: return

                val progress = AnimationComponent[animation].progress
                val deltaZoom = scaleEffect.delta * progress

                scaleEffect.currentScale = if (deltaZoom < 0) 0.5 + (1 + deltaZoom) / 2 else 1.0 + deltaZoom

                camera.zoom = scaleEffect.startZoom + deltaZoom

                if (progress == 1.0) {
                    camera.center = scaleEffect.newCenter
                    cameraEntity.removeComponent(CameraScaleEffectComponent::class)
                    UpdateViewProjectionComponent.tag(cameraEntity)
                }
            }
        }
    }

    class CameraScaleEffectComponent : EcsComponent {
        var animationId: Int = 0
        var scaleOrigin: DoubleVector? = null
        var newCenter: DoubleVector? = null
        var currentScale: Double = 0.toDouble()
        var delta: Double = 0.toDouble()
        var startZoom: Double = 0.toDouble()

        companion object {
            fun provide(cameraEntity: EcsEntity): CameraScaleEffectComponent {
                return cameraEntity.provideComponent(::CameraScaleEffectComponent)
            }

            operator fun get(cameraEntity: EcsEntity): CameraScaleEffectComponent {
                return cameraEntity.getComponent()
            }
        }
    }
}