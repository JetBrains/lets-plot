package jetbrains.livemap.core.ecs

interface EcsSystem {
    fun init(context: EcsContext)
    fun update(context: EcsContext, dt: Double)
    fun destroy()
}