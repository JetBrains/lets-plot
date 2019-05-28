package jetbrains.datalore.base.registration

import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.function.Value
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationMapTest {

    private var map: RegistrationMap<String>? = null

    @BeforeTest
    fun init() {
        map = RegistrationMap()
    }

    @Test
    fun dispose() {
        map!!.put("a", object : Registration() {
            override fun doRemove() {
                throw RuntimeException("test")
            }
        })
        map!!.put("b", object : Registration() {
            override fun doRemove() {}
        })
        map!!.put("c", object : Registration() {
            override fun doRemove() {}
        })

        try {
            map!!.clear()
        } catch (e: RuntimeException) {
            assertEquals("test", e.message)
        }

        assertTrue(map!!.keys().isEmpty())
    }

    @Test
    fun replace() {
        val value = Value(0)
        val key = "key"
        map!!.put(key, setMemoizeOldValue(value, 1))
        map!!.replace(key, object : Supplier<Registration> {
            override fun get(): Registration {
                return setMemoizeOldValue(value, 2)
            }
        })

        assertEquals(2, value.get())
    }

    private fun setMemoizeOldValue(valueHolder: Value<Int>, newValue: Int): Registration {
        val oldValue = valueHolder.get()
        valueHolder.set(newValue)
        return object : Registration() {
            override fun doRemove() {
                valueHolder.set(oldValue)
            }
        }
    }
}