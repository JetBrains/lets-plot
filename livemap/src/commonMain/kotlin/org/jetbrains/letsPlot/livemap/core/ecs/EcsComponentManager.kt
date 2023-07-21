/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.livemap.containers.singletonCollection
import kotlin.reflect.KClass

class EcsComponentManager {

    private val myEntityById = HashMap<Int, EcsEntity>()
    private val myComponentsByEntity = HashMap<EcsEntity, MutableMap<KClass<out EcsComponent>, EcsComponent>>()
    private val myEntitiesByComponent = HashMap<KClass<out EcsComponent>, MutableSet<EcsEntity>>()
    private val myRemovedEntities = ArrayList<EcsEntity>()
    private var myIdGenerator = 0

    internal val entities = myComponentsByEntity.keys
    val entitiesCount
        get() = myComponentsByEntity.size

    fun createEntity(name: String): EcsEntity {
        return EcsEntity(myIdGenerator++, name, this).also { entity ->
            myComponentsByEntity[entity] = entity.componentsMap
            myEntityById[entity.id] = entity
        }
    }

    fun getEntityById(entityId: Int): EcsEntity = findEntityById(entityId)!!

    fun getEntitiesById(ids: Collection<Int>): Sequence<EcsEntity> {
        return ids
            .asSequence()
            .mapNotNull { myEntityById[it] }
            .notRemoved()
    }

    fun findEntityById(entityId: Int): EcsEntity? =
        myEntityById[entityId]?.takeIf { !it.hasRemoveFlag() }

    /**
     * Returns entities containing component with [componentType] or empty sequence
     */
    fun getEntities(componentType: KClass<out EcsComponent>): Sequence<EcsEntity> =
        (myEntitiesByComponent[componentType] ?: emptySet<EcsEntity>()).notRemoved()

    /**
     * Add [component] to [entity].
     * Throws exception if component of this type is already added to the [entity]
     */
    internal fun <T : EcsComponent> addComponent(entity: EcsEntity, component: T) {
        val entityComponents = myComponentsByEntity[entity]
        require(entityComponents != null) { "addComponent to non existing entity" }
        require(component::class !in entityComponents) { "Entity already has component with the type " + component::class }

        entityComponents[component::class] = component
        myEntitiesByComponent.getOrPut(component::class, ::HashSet).add(entity)
    }

    /**
     * Return all components of the [entity] if it is not marked as removed
     */
    fun getComponents(entity: EcsEntity): Map<KClass<out EcsComponent>, EcsComponent> {
        if (entity.hasRemoveFlag()) {
            return emptyMap()
        }
        return myComponentsByEntity[entity] ?: emptyMap()
    }

    /**
     * Count number of components of type [componentType]. Components from removed entities are not counted.
     */
    fun count(componentType: KClass<out EcsComponent>): Int =
        myEntitiesByComponent[componentType]?.notRemoved()?.count() ?: 0

    fun containsEntity(componentType: KClass<out EcsComponent>): Boolean =
        myEntitiesByComponent.containsKey(componentType)


    /**
     * Returns true if [entity] exists and not removed.
     */
    fun containsEntity(entity: EcsEntity): Boolean =
        !entity.hasRemoveFlag() && myComponentsByEntity.containsKey(entity)

    /**
     * Return entities containing all components with [componentTypes] or empty sequence
     */
    fun getEntities(componentTypes: Collection<KClass<out EcsComponent>>): Sequence<EcsEntity> =
        getEntities(componentTypes.first()).filter { entity -> entity.contains(componentTypes) }

    /**
     * Returns single entity containing all [componentTypes] or null.
     * Throws exception if number of such entities is not one.
     */
    fun tryGetSingletonEntity(componentTypes: Collection<KClass<out EcsComponent>>): EcsEntity? {
        val entities = getEntities(componentTypes)

        check(entities.count() <= 1) { "Entity with specified components is not a singleton: $componentTypes" }

        return entities.firstOrNull()
    }

    /**
     * Returns single entity containing all [componentTypes].
     * Throws exception if number of such entities is not one.
     */
    fun getSingletonEntity(componentTypes: Collection<KClass<out EcsComponent>>): EcsEntity {
        val singleton = tryGetSingletonEntity(componentTypes)

        check(singleton != null) { "Entity with specified components does not exist: $componentTypes" }

        return singleton
    }

    /**
     * Return single entity containing [componentType].
     * Throws exception if number of such entities is not one.
     */
    fun getSingletonEntity(componentType: KClass<out EcsComponent>): EcsEntity =
        getSingletonEntity(singletonCollection(componentType))

    inline fun <reified ComponentT : EcsComponent> getSingletonEntity(): EcsEntity =
        getSingletonEntity(ComponentT::class)


    /**
     * Returns first entity, containing [componentType].
     * Order is undefined.
     * If no entity with given [componentType] found exception will be thrown.
     */
    fun getEntity(componentType: KClass<out EcsComponent>): EcsEntity =
        getEntities(componentType).firstOrNull() ?: error("Entity with specified component does not exist: $componentType")

    /**
     * Return single component of type [ComponentT].
     * Throws exception if exists more than one component instance.
     */
    inline fun <reified ComponentT : EcsComponent> getSingleton(): ComponentT =
        getSingletonEntity(ComponentT::class).getComponent()

    inline fun <reified ComponentT : EcsComponent> tryGetSingleton(): ComponentT? {
        if (containsEntity(ComponentT::class)) {
            return getSingleton()
        }
        return null
    }

    inline fun <reified ComponentT : EcsComponent> count(): Int = count(ComponentT::class)

    inline fun <reified ComponentT : EcsComponent> containsEntity(): Boolean = containsEntity(ComponentT::class)


    /**
     * Mark [entity] as removed. This method can be safely used while iterating entites via [getEntities].
     */
    internal fun removeEntity(entity: EcsEntity) {
        myRemovedEntities.add(entity.also { it.setRemoveFlag() })
    }

    /**
     * Remove component with type [componentType] from [entity].
     * This method can't be used while iterating over entities via [getEntities]. It's required
     * to make a defensive copy of such entities and then remove components from them.
     */
    internal fun removeComponent(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        removeEntityFromComponents(entity, componentType)
        getComponentsWithRemoved(entity)?.remove(componentType)
    }

    private fun getComponentsWithRemoved(entity: EcsEntity): MutableMap<KClass<out EcsComponent>, out EcsComponent>? {
        return myComponentsByEntity[entity]
    }

    internal fun doRemove() {
        myRemovedEntities.forEach { entity ->
            getComponentsWithRemoved(entity)?.forEach { (componentType, _) ->
                removeEntityFromComponents(entity, componentType)
            }
            myComponentsByEntity.remove(entity)
            myEntityById.remove(entity.id)
        }
        myRemovedEntities.clear()
    }

    private fun removeEntityFromComponents(entity: EcsEntity, componentType: KClass<out EcsComponent>) {
        myEntitiesByComponent[componentType]?.let { componentEntities ->
            componentEntities.remove(entity)
            if (componentEntities.isEmpty()) {
                myEntitiesByComponent.remove(componentType)
            }
        }
    }

    private fun Collection<EcsEntity>.notRemoved(): Sequence<EcsEntity> {
        return asSequence().filterNot(EcsEntity::hasRemoveFlag)
    }

    private fun Sequence<EcsEntity>.notRemoved(): Sequence<EcsEntity> {
        return filterNot { it.hasRemoveFlag() }
    }

    fun removeEntity(entityId: Int) {
        findEntityById(entityId)?.let(this::removeEntity)
    }
}
