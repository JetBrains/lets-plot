package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

class MappingContext {
    private val myMappers = HashMap<Any, Any>()
    private val myListeners = Listeners<MappingContextListener>()

    private val myProperties = HashMap<MappingContextProperty<*>, Any?>()

    internal val mappers: Set<Mapper<*, *>>
        get() {
            val mappers = HashSet<Mapper<*, *>>()
            for (source in myMappers.keys) {
                mappers.addAll(getMappers(source))
            }
            return mappers
        }

    fun addListener(l: MappingContextListener): Registration {
        return myListeners.add(l)
    }

    internal fun register(mapper: Mapper<*, *>) {
        if (mapper.isFindable) {
            val source = mapper.source!!
            if (!myMappers.containsKey(source)) {
                val mapper1 = mapper
                myMappers[source] = mapper1
            } else {
                val ms = myMappers[source]
                if (ms is MutableSet<*>) {
                    val mappers = ms as MutableSet<Mapper<*, *>>
                    mappers.add(mapper)
                } else {
                    val m = ms as Mapper<*, *>
                    val mappers = HashSet<Mapper<*, *>>()
                    mappers.add(m)
                    mappers.add(mapper)
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

    internal fun unregister(mapper: Mapper<*, *>) {
        if (mapper.isFindable) {
            val source = mapper.source!!
            if (!myMappers.containsKey(source)) {
                throw IllegalStateException()
            }
            val ms = myMappers[source]
            if (ms is Set<*>) {
                val mappers = ms as MutableSet<Mapper<*, *>>
                mappers.remove(mapper)
                if (mappers.size == 1) {
                    myMappers[source] = mappers.iterator().next()
                }
            } else {
                if (ms !== mapper) {
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

    fun <S> getMapper(ancestor: Mapper<*, *>, source: S): Mapper<in S, *>? {
        val result = getMappers(ancestor, source)
        if (result.isEmpty()) return null
        if (result.size > 1) {
            throw IllegalStateException("There are more than one mapper for $source")
        }
        return result.iterator().next()
    }

    fun <S> getMappers(ancestor: Mapper<*, *>, source: S): Set<Mapper<in S, *>> {
        val mappers = getMappers(source)
        var result: MutableSet<Mapper<in S, *>>? = null
        for (m in mappers) {
            if (Mappers.isDescendant(ancestor, m)) {
                if (result == null) {
                    if (mappers.size == 1) {
                        return setOf(m)
                    } else {
                        result = HashSet()
                    }
                }
                result.add(m)
            }
        }
        return result ?: emptySet()
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

    operator fun <ValueT> get(property: MappingContextProperty<ValueT>): ValueT {
        val value = myProperties[property] ?: throw IllegalStateException("Property $property wasn't found")
        return value as ValueT
    }

    operator fun contains(property: MappingContextProperty<*>): Boolean {
        return myProperties.containsKey(property)
    }

    fun <ValueT> remove(property: MappingContextProperty<ValueT>): ValueT {
        if (!myProperties.containsKey(property)) {
            throw IllegalStateException("Property $property wasn't found")
        }
        return myProperties.remove(property) as ValueT
    }

    private fun <S> getMappers(source: S): Set<Mapper<in S, *>> {
        if (!myMappers.containsKey(source as Any)) {
            return emptySet()
        }
        val mappersObject = myMappers.get(source)
        if (mappersObject is Mapper<*, *>) {
            val mapper = mappersObject as Mapper<in S, *>
            return setOf(mapper)
        } else {
            val result = HashSet<Mapper<in S, *>>()
            val mapperSet = mappersObject as Set<Mapper<in S, *>>
            for (m in mapperSet) {
                result.add(m)
            }
            return result
        }
    }
}