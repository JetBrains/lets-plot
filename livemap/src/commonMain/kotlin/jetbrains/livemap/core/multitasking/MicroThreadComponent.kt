package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class MicroThreadComponent(val microThread: MicroTask<Unit>, internal val quantumIterations: Int) : EcsComponent {
    companion object {
        operator fun get(entity: EcsEntity): MicroThreadComponent {
            return entity.getComponent()
        }
    }
}