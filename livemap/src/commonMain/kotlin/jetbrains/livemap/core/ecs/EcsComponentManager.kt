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
            .filter { o -> !o.hasRemoveFlag() }
            .asIterable()
    }

    fun getEntities(componentType: KClass<out EcsComponent>): Iterable<EcsEntity> =
        EcsIterator((myComponents[componentType] ?: HashSet()).iterator()).asSequence().asIterable()


    fun getEntities(componentTypes: List<KClass<out EcsComponent>>): Iterable<EcsEntity> {
        return Iterables.filter(getEntities(componentTypes[0])) { componentTypes in it }
    }

    fun getEntity(componentType: KClass<out EcsComponent>): EcsEntity {
        val iterator = getEntities(componentType).iterator()

        if (!iterator.hasNext()) {
            throw IllegalStateException("Entity with specified component does not exist: $componentType")
        }

        return iterator.next()
    }

    fun getSingletonEntity(componentType: KClass<out EcsComponent>): EcsEntity {
        return getSingletonEntity(listOf(componentType))
    }

    fun getSingletonEntity(componentTypes: List<KClass<out EcsComponent>>): EcsEntity {
        val iterator =
            Iterables.filter(getEntities(componentTypes[0])) { componentTypes in it }.iterator()

        if (!iterator.hasNext()) {
            throw IllegalStateException("Entity with specified components does not exist: $componentTypes")
        }

        val singleton = iterator.next()

        if (iterator.hasNext()) {
            throw IllegalStateException("Entity with specified components is not singleton: $componentTypes")
        }

        return singleton
    }

    inline fun <reified ComponentT : EcsComponent> getSingletonComponent(): ComponentT {
        return getEntity(ComponentT::class).getComponent()
    }

    internal fun <T : EcsComponent> addComponent(entity: EcsEntity, component: T) {
        val components = myEntities[entity] ?: throw IllegalStateException("No entity with the given id")

        if (components.put(component::class, component) != null) {
            throw IllegalStateException("Entity already has component with the type " + component::class)
        }

        myComponents.getOrPut(component::class, ::HashSet).add(entity)
    }

    fun getComponents(entity: EcsEntity): MutableMap<KClass<out EcsComponent>, out EcsComponent> {
        return myEntities.getOrElse(entity, ::HashMap)
    }

    internal fun removeEntity(entity: EcsEntity) {
        entity.setRemoveFlag()
        myRemovedEntities.add(entity)
    }

    internal fun removeComponent(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        removeEntityFromComponents(entity, componentType)
        getComponents(entity).remove(componentType)
    }

    internal fun doRemove() {
        myRemovedEntities.forEach { entity ->
            getComponents(entity).keys.forEach { componentType -> removeEntityFromComponents(entity, componentType) }
            myEntities.remove(entity)
            myEntitiesIndex.remove(entity.id)
        }
        myRemovedEntities.clear()
    }

    fun getComponentsCount(componentType: KClass<out EcsComponent>): Int {
        return myComponents.getOrElse(componentType, ::HashSet).size
    }

    fun containsSingletonEntity(componentType: KClass<out EcsComponent>): Boolean {
        return myComponents.containsKey(componentType)
    }

    private fun removeEntityFromComponents(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        myComponents[componentType]?.let { entities ->
            entities.remove(entity)
            if (entities.isEmpty()) {
                myComponents.remove(componentType)
            }
        }
    }

    fun containsEntity(entity: EcsEntity): Boolean {
        return myEntities.containsKey(entity)
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