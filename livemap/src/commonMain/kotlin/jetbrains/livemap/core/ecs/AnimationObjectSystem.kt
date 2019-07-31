package jetbrains.livemap.core.ecs


class AnimationObjectSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {
    override fun init(context: EcsContext) {}

    override fun update(context: EcsContext, dt: Double) {
        for (entity in getEntities(AnimationObjectComponent::class)) {
            val animationObjectComponent = entity.getComponent<AnimationObjectComponent>()
            val anim = animationObjectComponent.animation

            anim.time += dt
            anim.animate()
            if (anim.isFinished) {
                entity.removeComponent(AnimationObjectComponent::class)
            }
        }
    }
}
