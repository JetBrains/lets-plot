package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.EcsComponent

class MicroThreadComponent(
    val microThread: MicroTask<Unit>,
    internal val quantumIterations: Int
) : EcsComponent