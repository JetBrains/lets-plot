package jetbrains.livemap.core.ecs

import jetbrains.datalore.base.registration.Disposable
import jetbrains.livemap.core.MetricsService

class EcsController(
    private val myComponentManager: EcsComponentManager,
    private val myContext: EcsContext,
    private val mySystems: List<EcsSystem>
) : Disposable {
    private val myDebugService: MetricsService = myContext.metricsService

    init {
        mySystems.forEach { it.init(myContext) }
    }

    fun update(dt: Double) {
        myContext.startUpdate()

        myDebugService.reset()
        for (system in mySystems) {
            myDebugService.beginMeasureUpdate()
            system.update(myContext, dt)
            myDebugService.endMeasureUpdate(system)
        }

        myComponentManager.doRemove()
    }

    override fun dispose() {
        mySystems.forEach(EcsSystem::destroy)
    }
}