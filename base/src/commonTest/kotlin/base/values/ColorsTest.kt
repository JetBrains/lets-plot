package jetbrains.datalore.base.values

import kotlin.test.*

class ColorsTest {
    @Test
    fun namedColors() {
        assertTrue(Colors.isColorName("pink"))
        assertTrue(Colors.isColorName("pInk"))
        assertFalse(Colors.isColorName("unknown"))

        assertNotNull(Colors.forName("red"))
        assertNotNull(Colors.forName("rEd"))
    }

    @Test
    fun unknownColor() {
        assertFailsWith<IllegalArgumentException> {
            Colors.forName("unknown")
        }
    }
}
