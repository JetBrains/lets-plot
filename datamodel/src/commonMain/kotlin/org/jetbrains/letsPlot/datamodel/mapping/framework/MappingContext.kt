/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

class MappingContext {
    private val myMappers: MutableMap<in Any?, in Any?> = mutableMapOf()
    private val myListeners: Listeners<MappingContextListener> = Listeners()

    private val myProperties: MutableMap<MappingContextProperty<*>, Any?> = mutableMapOf()

    fun addListener(l: MappingContextListener): Registration = myListeners.add(l)

    fun register(mapper: Mapper<*, *>) {
        if (mapper.isFindable) {
            val source = mapper.source
            if (!(myMappers.containsKey(source))) {
                myMappers[source] = mapper
            } else {
                val ms = myMappers[source]
                if (ms is Set<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val mappers: MutableSet<Mapper<*, *>> = ms as MutableSet<Mapper<*, *>>
                    mappers.add(mapper)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    val m = ms as Mapper<*, *>
                    val mappers: MutableSet<Mapper<*, *>> = mutableSetOf(m, mapper)
                    myMappers[source] = mappers
                }
            }
        }

        myListeners.fire(object : ListenerCaller<MappingContextListener> {
            override fun call(l: MappingContextListener) {
                l.onMapperRegistered(mapper)
            }
        })
    }

    fun unregister(mapper: Mapper<*, *>) {
        if (mapper.isFindable) {
            val source = mapper.source
            if (!myMappers.containsKey(source)) {
                throw IllegalStateException()
            }
            val ms = myMappers[source]
            if (ms is Set<*>) {
                @Suppress("UNCHECKED_CAST")
                val mappers: MutableSet<Mapper<*, *>> = ms as MutableSet<Mapper<*, *>>
                mappers.remove(mapper)
                if (mappers.size == 1) {
                    myMappers[source] = mappers.iterator().next()
                }
            } else {
                if (ms != mapper) {
                    throw IllegalStateException()
                }
                myMappers.remove(source)
            }
        }

        myListeners.fire(object : ListenerCaller<MappingContextListener> {
            override fun call(l: MappingContextListener) {
                l.onMapperUnregistered(mapper)
            }
        })
    }

    fun <S> getMapper(ancestor: Mapper<*, *>, source: S): Mapper<in S, Any>? {
        val result: Set<Mapper<in S, Any>> = getMappers(ancestor, source)
        if (result.isEmpty()) return null
        if (result.size > 1) {
            throw IllegalStateException("There are more than one mapper for $source")
        }
        return result.iterator().next()
    }

    fun <S> getMappers(ancestor: Mapper<*, *>, source: S): Set<Mapper<in S, Any>> {
        val mappers = getMappers(source)
        var result: MutableSet<Mapper<in S, Any>>? = null
        for (m in mappers) {
            if (Mappers.isDescendant(ancestor, m)) {
                if (result == null) {
                    if (mappers.size == 1) {
                        return setOf(m)
                    } else {
                        result = mutableSetOf()
                    }
                }
                result.add(m)
            }
        }
        if (result == null) {
            return setOf()
        }
        return result
    }

    fun <ValueT> put(property: MappingContextProperty<ValueT>, value: ValueT?) {
        if (myProperties.containsKey(property)) {
            throw IllegalStateException("Property $property is already defined")
        }

        if (value == null) {
            throw IllegalArgumentException("Trying to set null as a value of $property")
        }

        myProperties[property] = value
    }

    fun <ValueT> get(property: MappingContextProperty<ValueT>): ValueT {
        val value = myProperties[property] ?: throw IllegalStateException("Property $property wasn't found")

        @Suppress("UNCHECKED_CAST")
        return value as ValueT
    }

    fun contains(property: MappingContextProperty<Any>) = myProperties.containsKey(property)

    fun <ValueT> remove(property: MappingContextProperty<Any>): ValueT {
        if (!myProperties.containsKey(property)) {
            throw IllegalStateException("Property $property wasn't found")
        }

        @Suppress("UNCHECKED_CAST")
        return myProperties.remove(property) as ValueT
    }

    fun getMappers(): Set<Mapper<in Any, Any>> {
        val mappers: MutableSet<Mapper<in Any, Any>> = mutableSetOf()
        for (source in myMappers.keys) {
            mappers.addAll(getMappers(source))
        }
        return mappers
    }

    private fun <S> getMappers(source: S): Set<Mapper<in S, Any>> {
        if (!myMappers.containsKey(source)) {
            return setOf()
        }
        val mappersObject = myMappers[source]
        if (mappersObject is Mapper<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val mapper = mappersObject as Mapper<in S, Any>
            return setOf(mapper)
        } else {
            val result: MutableSet<Mapper<in S, Any>> = mutableSetOf()
            @Suppress("UNCHECKED_CAST")
            val mapperSet = mappersObject as Set<Mapper<in S, Any>>
            for (m in mapperSet) {
                result.add(m)
            }
            return result
        }
    }
}