package demo

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformTestsJs {
    @Test
    fun testJs() {
        val name = PlatformClass().getName()
        println("Js test checking: $name")
        assertEquals(name, "(JS)")
    }
}