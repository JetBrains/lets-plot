/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.typedKey

/**
 * Maps typed keys to not-null values.
 * The type parametr can't be a nullable type.
 */
class TypedKeyHashMap private constructor(
    private val map: MutableMap<TypedKey<*>, Any?>
) {

    constructor() : this(hashMapOf<TypedKey<*>, Any?>())

    /**
     * Throws NoSuchElementException if key is not present.
     */
    operator fun <T> get(key: TypedKey<T>): T {
        if (map.containsKey(key)) {
            @Suppress("UNCHECKED_CAST")
            return map[key] as T
        }
        throw NoSuchElementException("Wasn't found key $key")
    }

    operator fun <T> set(key: TypedKey<T>, value: T?) {
        put(key, value)
    }

    /**
     * Null value is ignored and the key (if present) is removed from container.
     *
     * Note: the fact that the value type is nullable is very important when used with 'primitive'
     * Kotlin type (like Double) in 'native' context.
     * Without 'T?' Kotlin may choose to represent Double by double and will crash on an attempt to cast type (`as`) or
     * to apply null-safe operator (!!). Such behavior was observed in Kotlin native.
     * See Kotlin docs: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html
     */
    fun <T> put(key: TypedKey<T>, value: T?) {
        if (value == null) {
            map.remove(key)
        } else {
            map[key] = value
        }
    }

    fun contains(key: TypedKey<*>): Boolean {
        return containsKey(key)
    }

    fun <T> containsKey(key: TypedKey<T>): Boolean {
        return map.containsKey(key)
    }

    fun <T> keys(): Set<TypedKey<T>> {
        @Suppress("UNCHECKED_CAST")
        return map.keys as Set<TypedKey<T>>
    }

    fun makeCopy(): TypedKeyHashMap {
        return TypedKeyHashMap(HashMap(this.map))
    }
}
