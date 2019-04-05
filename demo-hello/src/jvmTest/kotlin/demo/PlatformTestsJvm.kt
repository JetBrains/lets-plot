package demo

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformTestsJvm {
    @Test
    fun testJvm() {
        val name = PlatformClass().getName()
        println("JVM test checking: $name")
        assertEquals(name, "(JVM)")
    }
}