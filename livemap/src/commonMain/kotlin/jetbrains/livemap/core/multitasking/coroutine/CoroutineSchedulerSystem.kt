package jetbrains.livemap.core.multitasking.coroutine

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext


class CoroutineSchedulerSystem(
    private val coroutineTaskExecutor: CoroutineTaskExecutor,
    componentManager: EcsComponentManager
) : AbstractSystem<EcsContext>(componentManager) {

    private val disp = CooperativeCoroutineDispatcher()
    var loading: Long = 0
        private set

    override fun updateImpl(context: EcsContext, dt: Double) {

        if (componentManager.getComponentsCount(MicroCoThreadComponent::class) > 0) {
            val microThreadEntities = getEntities<MicroCoThreadComponent>().toList().asSequence()

            val finishedTasks = microThreadEntities
                .map { it.get<MicroCoThreadComponent>() }
                .run (coroutineTaskExecutor::execute)


            microThreadEntities
                .filter { it.get<MicroCoThreadComponent>() in finishedTasks }
                .forEach {it.remove<MicroCoThreadComponent>() }

            loading = context.systemTime.getTimeMs() - context.updateStartTime
        } else {
            loading = 0
        }

    }
}