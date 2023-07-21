/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.containers

class LruCache<K, E>(private val limit: Int) {
    val values: List<E>
        get() {
            val resultList = ArrayList<E>()

            var node: Node<K, E>? = head ?: return emptyList()
            while (node != null) {
                resultList.add(node.myItem)

                node = node.myNext
            }

            return resultList
        }

    private val map = HashMap<K, Node<K, E>>()
    private var head: Node<K, E>? = null
    private var tail: Node<K, E>? = null

    private fun nodeToHead(node: Node<K, E>) {
        if (node !== head) {
            if (node === tail) {
                tail = node.myPrev
            }

            node.myPrev?.myNext = node.myNext
            node.myNext?.myPrev = node.myPrev

            node.myNext = head
            head!!.myPrev = node

            head = node
        }
    }

    operator fun get(key: K): E? {

        return map[key]?.let { node ->
            nodeToHead(node)
            node.myItem
        }
    }

    fun put(key: K, value: E) {
        map[key]
            ?.let {
                it.myItem = value
                nodeToHead(it)
            }
            ?:let {
                if (map.isNotEmpty()) {
                    head!!.myPrev = Node(key, value, null, head)
                    head!!.myPrev
                } else {
                    tail = Node(key, value, null, null)
                    tail
                }.let {
                    requireNotNull(it)
                    head = it
                }

                map[key] = head!!
            }

        if (map.size > limit) {
            tail?.let {
                tail = it.myPrev
                tail!!.myNext = null
                map.remove(it.myKey)
            }
        }
    }

    fun getOrPut(key: K, defaultValue: () -> E): E {
        val value = get(key)

        return if (value != null) {
            value
        } else {
            val answer = defaultValue()
            put(key, answer)

            answer
        }
    }

    fun containsKey(key: K): Boolean = map.containsKey(key)

    private class Node<K, E> internal constructor(
        internal val myKey: K,
        internal var myItem: E,
        internal var myPrev: Node<K, E>?,
        internal var myNext: Node<K, E>?
    )
}
