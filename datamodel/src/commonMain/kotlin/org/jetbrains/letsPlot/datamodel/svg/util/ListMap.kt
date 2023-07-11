/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.util

/**
 * Memory efficient implementation of a map based on an array.
 *
 *
 * It works better than a HashMap and TreeMap on small sized collections.
 */
internal class ListMap<K, V> {

    companion object {
        private val EMPTY_ARRAY = arrayOfNulls<Any>(0)
    }

    private var myData = EMPTY_ARRAY

    val isEmpty: Boolean
        get() = size() == 0

    fun containsKey(key: K): Boolean {
        return findByKey(key) >= 0
    }

    fun remove(key: K): V? {
        val index = findByKey(key)
        if (index >= 0) {
            val value = myData[index + 1]
            removeAt(index)
            @Suppress("UNCHECKED_CAST")
            return value as V?
        } else {
            return null
        }
    }

    fun keySet(): MutableSet<K> {
        return object : AbstractMutableSet<K>() {
            override val size: Int
                get() = this@ListMap.size()

            override fun add(element: K): Boolean {
                throw IllegalStateException("Not available in keySet")
            }

            override fun iterator(): MutableIterator<K> {
                return mapIterator(object : IteratorSpec {
                    override operator fun get(index: Int): Any? {
                        return myData[index]
                    }
                })
            }
        }
    }

    fun values(): Collection<V?> {
        return object : AbstractCollection<V?>() {
            override val size: Int
                get() = this@ListMap.size()

            override fun iterator(): Iterator<V?> {
                return mapIterator(object : IteratorSpec {
                    override operator fun get(index: Int): Any? {
                        return myData[index + 1]
                    }
                })
            }
        }
    }

    fun entrySet(): Set<Entry> {
        return object : AbstractSet<Entry>() {
            override val size: Int
                get() = this@ListMap.size()

            override fun iterator(): Iterator<ListMap<K, V>.Entry> {
                return mapIterator(object : IteratorSpec {
                    override operator fun get(index: Int): Any {
                        return Entry(index)
                    }
                })
            }
        }
    }

    fun size(): Int {
        return myData.size / 2
    }

    fun put(key: K, value: V?): V? {
        val index = findByKey(key)
        if (index >= 0) {
            val oldValue = myData[index + 1]
            myData[index + 1] = value
            @Suppress("UNCHECKED_CAST")
            return oldValue as V?
        }

        val newArray = Array(myData.size + 2) { i ->
            if (i < myData.size) myData[i] else null
        }

        newArray[myData.size] = key
        newArray[myData.size + 1] = value
        myData = newArray
        return null
    }

    operator fun get(key: K): V? {
        val index = findByKey(key)
        return if (index == -1) {
            null
        } else {
            @Suppress("UNCHECKED_CAST")
            myData[index + 1] as V?
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{")
        var i = 0
        while (i < myData.size) {
            val k = myData[i]
            val v = myData[i + 1]
            if (i != 0) {
                builder.append(",")
            }
            builder.append(k).append("=").append(v)
            i += 2
        }
        builder.append("}")

        return builder.toString()
    }

    private fun <T> mapIterator(spec: IteratorSpec): MutableIterator<T> {
        return object : MutableIterator<T> {
            private var index = 0
            private var nextCalled = false

            override fun hasNext(): Boolean {
                return index < myData.size
            }

            override fun next(): T {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                nextCalled = true
                @Suppress("UNCHECKED_CAST")
                val value = spec[index] as T
                index += 2
                return value
            }

            override fun remove() {
                if (!nextCalled) {
                    throw IllegalStateException()
                }
                index -= 2
                removeAt(index)
                nextCalled = false
            }
        }
    }

    private fun findByKey(key: K): Int {
        var i = 0
        while (i < myData.size) {
            val k = myData[i]
            if (key == k) {
                return i
            }
            i += 2
        }
        return -1
    }

    private fun removeAt(index: Int) {
        if (myData.size == 2) {
            myData = EMPTY_ARRAY
            return
        }

        val newArray = Array(myData.size - 2) { i ->
            if (i < index) {
                myData[i]
            } else {
                myData[i + 2]
            }
        }

        myData = newArray
    }

    inner class Entry internal constructor(private val myIndex: Int) {
        fun key(): K {
            @Suppress("UNCHECKED_CAST")
            return myData[myIndex] as K
        }

        fun value(): V? {
            @Suppress("UNCHECKED_CAST")
            return myData[myIndex + 1] as V?
        }
    }

    private interface IteratorSpec {
        operator fun get(index: Int): Any?
    }
}
