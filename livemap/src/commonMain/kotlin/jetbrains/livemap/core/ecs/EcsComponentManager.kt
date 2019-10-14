package jetbrains.livemap.core.ecs

import jetbrains.datalore.base.gcommon.collect.Iterables
import kotlin.reflect.KClass

class EcsComponentManager {

    private val myEntitiesIndex = HashMap<Int, EcsEntity>()
    private val myEntities = HashMap<EcsEntity, MutableMap<KClass<out EcsComponent>, EcsComponent>>()
    private val myComponents = HashMap<KClass<out EcsComponent>, MutableSet<EcsEntity>>()
    private val myRemovedEntities = ArrayList<EcsEntity>()
    private var myIdGenerator = 0

    internal val entities = myEntities.keys
    val entitiesCount = myEntities.size

    fun createEntity(name: String): EcsEntity {
        val entityComponents = HashMap<KClass<out EcsComponent>, EcsComponent>()
        val entity = EcsEntity(myIdGenerator++, name, this, entityComponents)
        myEntities[entity] = entityComponents
        myEntitiesIndex[entity.id] = entity
        return entity
    }

    fun getEntityById(entityId: Int): EcsEntity {
        return myEntitiesIndex[entityId]?.takeIf { !it.hasRemoveFlag() }!!
    }

    fun getEntitiesById(ids: Collection<Int>): Iterable<EcsEntity> {
        return ids.asSequence()
            .map { myEntitiesIndex[it] }
            .filterNotNull()
            .filter { !it.hasRemoveFlag() }
            .asIterable()
    }

    fun getEntities(componentType: KClass<out EcsComponent>): Iterable<EcsEntity> =
        EcsIterator((myComponents[componentType] ?: emptySet<EcsEntity>()).iterator()).asSequence().asIterable()


    fun getEntities(componentTypes: List<KClass<out EcsComponent>>): Iterable<EcsEntity> {
        return Iterables.filter(getEntities(componentTypes[0])) { componentTypes in it }
    }

    /**
     * Returns first of all entities, containing [componentType].
     * Order is undefined.
     */
    fun getEntity(componentType: KClass<out EcsComponent>): EcsEntity {
        val iterator = getEntities(componentType).iterator()

        check(iterator.hasNext()) { "Entity with specified component does not exist: $componentType" }

        return iterator.next()
    }


    fun getSingletonEntity(componentType: KClass<out EcsComponent>): EcsEntity {
        return getSingletonEntity(listOf(componentType))
    }

    /**
     * Returns single entity, containing [componentTypes].
     * Throws exception if exists more than one entity.
     */
    fun getSingletonEntity(componentTypes: List<KClass<out EcsComponent>>): EcsEntity {
        val iterator =
            Iterables.filter(getEntities(componentTypes[0])) { componentTypes in it }.iterator()

        check(iterator.hasNext()) { "Entity with specified components does not exist: $componentTypes" }

        val singleton = iterator.next()

        check(!iterator.hasNext()) { "Entity with specified components is not singleton: $componentTypes" }

        return singleton
    }


    /**
     * Return single component of type [ComponentT].
     * Throws exception if exists more than one component instance.
     */
    inline fun <reified ComponentT : EcsComponent> getSingletonComponent(): ComponentT {
        return getEntity(ComponentT::class).getComponent()
    }

    /**
     * Add [component] to [entity].
     * Throws exception if component of this type is already added to the [entitiy]
     */
    internal fun <T : EcsComponent> addComponent(entity: EcsEntity, component: T) {
        val components = myEntities[entity] ?: throw IllegalStateException("No entity with the given id")

        check(components.put(component::class, component) == null) { "Entity already has component with the type " + component::class }

        myComponents.getOrPut(component::class, ::HashSet).add(entity)
    }

    /**
     * Return all components of the [entity] if it is not marked as removed
     */
    fun getComponents(entity: EcsEntity): Map<KClass<out EcsComponent>, EcsComponent> {
        return getComponentsInternal(entity) ?: emptyMap()
    }

    /**
     * Mark [entity] as removed. This method can be safely used while iterating entites via [getEntities].
     */
    internal fun removeEntity(entity: EcsEntity) {
        entity.setRemoveFlag()
        myRemovedEntities.add(entity)
    }

    /**
     * Remove component with type [componentType] from [entity].
     * This method can't be used while iterating over entities via [getEntities]. It's required
     * to make a defensive copy of such entities and then remove components from them.
     */
    internal fun removeComponent(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        removeEntityFromComponents(entity, componentType)
        getComponentsInternal(entity)?.remove(componentType)
    }

    private fun getComponentsInternal(entity: EcsEntity): MutableMap<KClass<out EcsComponent>, out EcsComponent>? {
        if (entity.hasRemoveFlag()) {
            return null
        }
        return myEntities.get(entity)
    }

    internal fun doRemove() {
        myRemovedEntities.forEach { entity ->
            getComponents(entity).keys.forEach { componentType -> removeEntityFromComponents(entity, componentType) }
            myEntities.remove(entity)
            myEntitiesIndex.remove(entity.id)
        }
        myRemovedEntities.clear()
    }

    /**
     * Count number of components of type [componentType]. Components from removed entities are not counted.
     */
    fun getComponentsCount(componentType: KClass<out EcsComponent>): Int {
        return myComponents.get(componentType)?.count { !it.hasRemoveFlag() } ?: 0
    }

    fun containsSingletonEntity(componentType: KClass<out EcsComponent>): Boolean {
        return myComponents.containsKey(componentType)
    }

    /**
     * Returns true if [entity] exists and not removed.
     */
    fun containsEntity(entity: EcsEntity): Boolean {
        return !entity.hasRemoveFlag() && myEntities.containsKey(entity)
    }

    private fun removeEntityFromComponents(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        myComponents[componentType]?.let { entities ->
            entities.remove(entity)
            if (entities.isEmpty()) {
                myComponents.remove(componentType)
            }
        }
    }

    private class EcsIterator<T : EcsRemovable> (private val myIterator: Iterator<T>) : AbstractIterator<T>() {

        override fun computeNext() {
            while (myIterator.hasNext()) {
                val entity = myIterator.next()
                if (!entity.hasRemoveFlag()) {
                    return setNext(entity)
                }
            }
            return done()
        }
    }
}