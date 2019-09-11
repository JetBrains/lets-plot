package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext

class SchedulerSystem(private val microTaskExecutor: MicroTaskExecutor, componentManager: EcsComponentManager) :
    AbstractSystem<EcsContext>(componentManager) {
    var loading: Long = 0
        private set

    override fun initImpl(context: EcsContext) {
        microTaskExecutor.start()
    }

    override fun updateImpl(context: EcsContext, dt: Double) {
        if (componentManager.getComponentsCount(MicroThreadComponent::class) > 0) {
            val microThreadEntities = getEntities(MicroThreadComponent::class).toList()
            val tasks = HashSet<MicroThreadComponent>()
            microThreadEntities.forEach { tasks.add(it.get<MicroThreadComponent>()) }

            val finishedTasks = microTaskExecutor.updateAndGetFinished(tasks)

            microThreadEntities.forEach {
                if (finishedTasks.contains(it.get<MicroThreadComponent>())) {
                    it.removeComponent(MicroThreadComponent::class)
                }
            }

            loading = context.systemTime.getTimeMs() - context.updateStartTime
        } else {
            loading = 0
        }
    }

    override fun destroy() {
        microTaskExecutor.stop()
    }
}