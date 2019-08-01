package jetbrains.livemap.core.ecs

import kotlin.jvm.Synchronized
import kotlin.reflect.KClass

abstract class AbstractSystem<T : EcsContext> protected constructor(val componentManager: EcsComponentManager) :
    EcsSystem {
    private val myTasks = ArrayList<() -> Unit>()

    override fun init(context: EcsContext) {
        @Suppress("UNCHECKED_CAST")
        initImpl(context as T)
    }


    override fun update(context: EcsContext, dt: Double) {
        executeTasks()

        @Suppress("UNCHECKED_CAST")
        updateImpl(context as T, dt)
    }

    override fun destroy() {}

    protected open fun initImpl(context: T) {}

    protected open fun updateImpl(context: T, dt: Double) {}

    inline fun <reified T: EcsComponent> getEntities(): Iterable<EcsEntity> {
        return componentManager.getEntities(T::class)
    }

    fun getEntities(componentType: KClass<out EcsComponent>): Iterable<EcsEntity> {
        return componentManager.getEntities(componentType)
    }

    fun getEntities(componentTypes: List<KClass<out EcsComponent>>): Iterable<EcsEntity> {
        return componentManager.getEntities(componentTypes)
    }

    fun getEntityById(entityId: Int): EcsEntity? {
        return componentManager.getEntityById(entityId)
    }

    fun getEntitiesById(entitiesId: Collection<Int>): Iterable<EcsEntity> {
        return componentManager.getEntitiesById(entitiesId)
    }

    fun getSingletonEntity(componentType: KClass<out EcsComponent>): EcsEntity {
        return componentManager.getEntity(componentType)
    }

    fun containsSingletonEntity(componentType: KClass<out EcsComponent>): Boolean {
        return componentManager.containsSingletonEntity(componentType)
    }

    inline fun <reified ComponentT : EcsComponent> getSingletonComponent(componentType: KClass<ComponentT>): ComponentT {
        return componentManager.getSingletonComponent()
    }

    fun getSingletonEntity(componentTypes: List<KClass<out EcsComponent>>): EcsEntity {
        return componentManager.getSingletonEntity(componentTypes)
    }

    protected fun createEntity(name: String): EcsEntity {
        return componentManager.createEntity(name)
    }

    @Synchronized
    protected fun runLaterBySystem(entity: EcsEntity, entityHandler: (EcsEntity) -> Unit) {
        myTasks.add {
            if (componentManager.containsEntity(entity)) {
                entityHandler(entity)
            }
        }
    }

    @Synchronized
    private fun fetchTasks(): List<() -> Unit> {
        if (myTasks.isEmpty()) {
            return emptyList()
        }
        return ArrayList(myTasks).also { myTasks.clear() }
    }

    private fun executeTasks() {
        fetchTasks().forEach{ it() }
    }
}