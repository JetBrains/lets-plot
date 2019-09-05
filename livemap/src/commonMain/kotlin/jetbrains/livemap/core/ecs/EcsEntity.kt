package jetbrains.livemap.core.ecs

import kotlin.reflect.KClass

class EcsEntity internal constructor(
    internal val id: Int,
    val name: String,
    val componentManager: EcsComponentManager,
    componentsMap: Map<KClass<out EcsComponent>, EcsComponent>
) : EcsRemovable() {
    private val components: Collection<EcsComponent> = componentsMap.values // for debug

    override fun toString(): String {
        return name
    }

    inline fun <reified T : EcsComponent>get(): T {
        return componentManager.getComponents(this)[T::class] as T? ?: throw IllegalStateException("Component " + T::class.simpleName + " is not found")
    }

    inline fun <reified T : EcsComponent>tryGet(): T? {
        if (!contains(T::class)) {
            return null
        }
        return get()
    }

    inline fun <reified T : EcsComponent> getComponent(): T {
        return componentManager.getComponents(this)[T::class] as T? ?: throw IllegalStateException("Component " + T::class.simpleName + " is not found")
    }

    inline fun <reified T : EcsComponent> provide(byDefault: () -> T): T {
        if (!contains(T::class)) {
            return byDefault().also { addComponent(it) }
        }

        return getComponent()
    }

    fun <T : EcsComponent> addComponent(component: T): EcsEntity {
        componentManager.addComponent(this, component)
        return this
    }

    fun <T : EcsComponent> setComponent(component: T): EcsEntity {
        if (this.contains(component::class)) {
            componentManager.removeComponent(this, component::class)
        }

        componentManager.addComponent(this, component)

        return this
    }

    fun removeComponent(componentType: KClass<out EcsComponent>) =
        apply { componentManager.removeComponent(this, componentType) }

    fun remove() {
        componentManager.removeEntity(this)
    }

    operator fun contains(componentType: KClass<out EcsComponent>): Boolean {
        return componentManager.getComponents(this).containsKey(componentType)
    }


    operator fun contains(componentTypes: Array<KClass<out EcsComponent>>): Boolean {
        val entityComponents = componentManager.getComponents(this)
        for (component in componentTypes) {
            if (!entityComponents.containsKey(component)) {
                return false
            }
        }

        return true
    }

    operator fun contains(components: List<KClass<out EcsComponent>>): Boolean {
        val entityComponents = componentManager.getComponents(this)
        for (component in components) {
            if (!entityComponents.containsKey(component)) {
                return false
            }
        }

        return true
    }
}
