package jetbrains.livemap.core.ecs

import kotlin.reflect.KClass

class EcsEntity internal constructor(
    internal val id: Int,
    val name: String,
    val componentManager: EcsComponentManager
) : EcsRemovable() {
    internal val componentsMap = HashMap<KClass<out EcsComponent>, EcsComponent>()
    private val components: Collection<EcsComponent> get() = componentsMap.values // for debug

    override fun toString(): String {
        return name
    }

    inline fun <reified T : EcsComponent>get(): T {
        return componentManager.getComponents(this)[T::class] as T? ?: throw IllegalStateException("Component " + T::class.simpleName + " is not found")
    }

    inline fun <reified T : EcsComponent>tryGet(): T? {
        return when {
            contains(T::class) -> get()
            else -> null
        }
    }

    inline fun <reified T : EcsComponent> provide(byDefault: () -> T): T {
        return when {
            contains(T::class) -> get()
            else -> byDefault().also { addComponent(it) }
        }
    }

    @Deprecated("Use <addComponents { + component }>")
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
    

    fun removeComponent(componentType: KClass<out EcsComponent>) = apply {
        componentManager.removeComponent(this, componentType)
    }

    /**
     * Mark [entity] as removed. This method can be safely used while iterating entites.
     */
    fun remove() = componentManager.removeEntity(this)

    operator fun contains(componentType: KClass<out EcsComponent>): Boolean =
        componentManager.getComponents(this).containsKey(componentType)

    operator fun contains(components: Collection<KClass<out EcsComponent>>): Boolean  =
        componentManager.getComponents(this).keys.containsAll(components)

    inline fun <reified T : EcsComponent> getComponent(): T  = get()
    inline fun <reified T: EcsComponent> contains() = contains(T::class)
    inline fun <reified T: EcsComponent> remove() = apply { removeComponent(T::class) }
    inline fun <reified T : EcsComponent> tag(supplier: () -> T) = provide(supplier)
    inline fun <reified T : EcsComponent> untag() = removeComponent(T::class)
}


class ComponentsList {
    val components = ArrayList<EcsComponent>()
    operator fun EcsComponent.unaryPlus() {
        components.add(this)
    }
}

fun EcsEntity.addComponents(block: ComponentsList.() -> Unit): EcsEntity {
    ComponentsList()
        .apply(block)
        .components
        .forEach { this.componentManager.addComponent(this, it) }

    return this
}
