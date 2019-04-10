package jetbrains.datalore.base.listMap

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListMapTest {

    companion object {
        private val RANDOM_STEPS = 100000
    }

    private val list = ListMap<String?, String>()
    private val map = HashMap<String?, String?>()
    private val random = Random(1000)

    @Test
    fun empty() {
        assertEquals("{}", list.toString())
    }

    @Test
    fun put() {
        list.put("a", "b")
        assertEquals("{a=b}", list.toString())
    }

    @Test
    fun putNull() {
        list.put("a", null)
        assertEquals("{a=null}", list.toString())
    }

    @Test
    fun nullKey() {
        list.put(null, "b")
        assertEquals("{null=b}", list.toString())
    }

    @Test
    fun removeKey() {
        list.put("a", "b")
        list.remove("a")
        assertEquals("{}", list.toString())
    }

    @Test
    fun isEmpty() {
        assertTrue(list.isEmpty)
    }

    @Test
    fun isEmptyAfterRemove() {
        list.put("a", "b")
        list.remove("a")
        assertTrue(list.isEmpty)
    }

    @Test
    fun notEmpty() {
        list.put("a", "b")
        assertFalse(list.isEmpty)
    }

    @Test
    fun valueOfRemove() {
        list.put("a", "b")
        assertEquals("b", list.remove("a"))
    }

    @Test
    fun containsKey() {
        list.put("a", "b")
        assertTrue(list.containsKey("a"))
    }

    @Test
    fun notContainsKey() {
        list.put("a", "b")
        assertFalse(list.containsKey("b"))
    }

    @Test
    fun keySetRemove() {
        list.put("a", "b")
        list.put("c", "d")
        val it = list.keySet().iterator()
        it.next()
        it.next()
        it.remove()
        assertEquals("{a=b}", list.toString())
    }

    @Test
    fun random() {
        for (i in 0 until RANDOM_STEPS) {
            doNextOp()
            assertEquals(map.size, list.size())
        }
        assertEquals(map.keys, list.keySet())
        val mapValues = ArrayList(map.values) as ArrayList<String>
        mapValues.sort()
        val listValues = ArrayList(list.values()) as ArrayList<String>
        listValues.sort()
        assertEquals(mapValues, listValues)

        assertEquals(map.keys, entriesToKeys(list.entrySet()))
        val listValuesFromEntries = entriesToValues(list.entrySet()) as MutableList<String>
        listValuesFromEntries.sort()
        assertEquals(mapValues, listValuesFromEntries)
    }

    private fun doNextOp() {
        val r = random.nextInt(3)
        when (r) {
            0 -> doDelete()
            1, 2 -> doInsert()
            else -> throw IllegalStateException()
        }
    }

    private fun doInsert() {
        val toInsertKey = nextString()
        val toInsertValue = nextString()
        assertEquals(map.put(toInsertKey, toInsertValue), list.put(toInsertKey, toInsertValue))
    }

    private fun doDelete() {
        val toDelete = nextString()
        assertEquals(map.remove(toDelete), list.remove(toDelete))
    }

    private fun nextString(): String {
        val index = random.nextInt(10)
        return index.toString()
    }

    private fun entriesToKeys(entries: Set<ListMap<String?, String>.Entry>): Set<String?> {
        val keys = HashSet<String?>()
        for (entry in entries) {
            keys.add(entry.key())
        }
        return keys
    }

    private fun entriesToValues(entries: Set<ListMap<String?, String>.Entry>): MutableList<String?> {
        val values = ArrayList<String?>(entries.size)
        for (entry in entries) {
            values.add(entry.value())
        }
        return values
    }
}
