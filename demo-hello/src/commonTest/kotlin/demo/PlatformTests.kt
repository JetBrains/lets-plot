package demo

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTests {
    @Test
    fun testCommon() {
        val name = PlatformClass().getName()
        println("Common test checking: $name")
        assertTrue(name.contains("J"))
    }
}