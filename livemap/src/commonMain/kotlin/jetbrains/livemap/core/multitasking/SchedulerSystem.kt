package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext

class SchedulerSystem(
    private val microTaskExecutor: MicroTaskExecutor,
    componentManager: EcsComponentManager
) : AbstractSystem<EcsContext>(componentManager) {
    var loading: Long = 0
        private set

    override fun initImpl(context: EcsContext) {
        microTaskExecutor.start()
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        if (componentManager.getComponentsCount(MicroThreadComponent::class) > 0) {
            val microThreadEntities = getEntities(MicroThreadComponent::class).toList().asSequence()

            val finishedTasks = microThreadEntities
                .map { it.get<MicroThreadComponent>() }
                .toHashSet()
                .run (microTaskExecutor::updateAndGetFinished)


            microThreadEntities
                .filter { it.get<MicroThreadComponent>() in finishedTasks }
                .forEach {it.remove<MicroThreadComponent>() }

            loading = context.systemTime.getTimeMs() - context.updateStartTime
        } else {
            loading = 0
        }
    }

    override fun destroy() {
        microTaskExecutor.stop()
    }
}