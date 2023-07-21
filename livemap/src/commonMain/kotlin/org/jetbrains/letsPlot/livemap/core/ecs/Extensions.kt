/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

inline fun <reified T1: EcsComponent, reified T2: EcsComponent> AbstractSystem<*>.getEntities2(): Sequence<EcsEntity> {
    return componentManager.getEntities(T1::class).filter { it.contains<T2>() }
}

inline fun <reified T1: EcsComponent, reified T2: EcsComponent, reified T3: EcsComponent> AbstractSystem<*>.getEntities3(): Sequence<EcsEntity> {
    return componentManager.getEntities(T1::class).filter { it.contains<T2>() && it.contains<T3>()}
}

inline fun <reified T: EcsComponent> AbstractSystem<*>.onEachEntity(proc: (EcsEntity, T) -> Unit) {
    componentManager.onEachEntity(proc)
}

inline fun <reified T1: EcsComponent, reified T2: EcsComponent> AbstractSystem<*>.onEachEntity2(proc: (EcsEntity, T1, T2) -> Unit) {
    componentManager.onEachEntity2(proc)
}

inline fun <reified T1: EcsComponent, reified T2: EcsComponent, reified T3: EcsComponent> AbstractSystem<*>.onEachEntity3(proc: (EcsEntity, T1, T2, T3) -> Unit) {
    componentManager.onEachEntity3(proc)
}

inline fun <reified T: EcsComponent> EcsComponentManager.onEachEntity(proc: (EcsEntity, T) -> Unit) {
    for (entity in getEntities(T::class)) {
        proc(entity, entity.get())
    }
}

inline fun <reified T1: EcsComponent, reified T2: EcsComponent> EcsComponentManager.onEachEntity2(proc: (EcsEntity, T1, T2) -> Unit) {
    for (entity in getEntities(listOf(T1::class, T2::class))) {
        proc(entity, entity.get<T1>(), entity.get<T2>())
    }
}

inline fun <reified T1: EcsComponent, reified T2: EcsComponent, reified T3: EcsComponent> EcsComponentManager.onEachEntity3(proc: (EcsEntity, T1, T2, T3) -> Unit) {
    for (entity in getEntities(listOf(T1::class, T2::class, T3::class))) {
        proc(entity, entity.get<T1>(), entity.get<T2>(), entity.get<T3>())
    }
}
