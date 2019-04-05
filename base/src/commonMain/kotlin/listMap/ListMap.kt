package jetbrains.datalore.base.listMap

/**
 * Memory efficient implementation of a map based on an array.
 *
 *
 * It works better than a HashMap and TreeMap on small sized collections.
 */
class ListMap<K, V> {

    private var myData = EMPTY_ARRAY

    val isEmpty: Boolean
        get() = size() == 0

    fun containsKey(key: K): Boolean {
        return findByKey(key) >= 0
    }

    fun remove(key: K): V? {
        val index = findByKey(key)
        if (index >= 0) {
            val value = myData[index + 1] as V
            removeAt(index)
            return value
        } else {
            return null
        }
    }

    fun keySet(): Set<K> {
        return object : AbstractSet<K>() {
            override val size: Int
                get() = this@ListMap.size()

            override fun iterator(): Iterator<K> {
                return mapIterator(object : IteratorSpec {
                    override operator fun get(index: Int): Any {
                        return myData[index]!!
                    }
                })
            }
        }
    }

    fun values(): Collection<V> {
        return object : AbstractCollection<V>() {
            override val size: Int
                get() = this@ListMap.size()

            override fun iterator(): Iterator<V> {
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

    fun put(key: K, value: V): V? {
        val index = findByKey(key)
        if (index >= 0) {
            val oldValue = myData[index + 1] as V
            myData[index + 1] = value
            return oldValue
        }

//        val newArray = arrayOfNulls<Any>(myData.size + 2)
//        System.arraycopy(myData, 0, newArray, 0, myData.size)

        val newArray = Array(myData.size + 2) { i ->
            myData[i]
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
        } else myData[index + 1] as V
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{")
        var i = 0
        while (i < myData.size) {
            val k = myData[i] as K
            val v = myData[i + 1] as V
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
            val k = myData[i] as K
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

//        val newArray = arrayOfNulls<Any>(myData.size - 2)
//        System.arraycopy(myData, 0, newArray, 0, index)
//        System.arraycopy(myData, index + 2, newArray, index, myData.size - index - 2)

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
            return myData[myIndex] as K
        }

        fun value(): V {
            return myData[myIndex + 1] as V
        }
    }

    private interface IteratorSpec {
        operator fun get(index: Int): Any?
    }

    companion object {
        private val EMPTY_ARRAY = arrayOfNulls<Any>(0)
    }
}
