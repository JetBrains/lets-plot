package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class MicroThreadComponent(
    val microThread: MicroTask<Unit>,
    internal val quantumIterations: Int
) : EcsComponent

fun EcsEntity.setMicroThread(i: Int, f: MicroTask<Unit>) {
    this.setComponent(MicroThreadComponent(f, i))
}