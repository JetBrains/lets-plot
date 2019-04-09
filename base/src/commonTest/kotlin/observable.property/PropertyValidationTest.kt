package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.function.Predicate
import kotlin.test.*

class PropertyValidationTest {
    @Test
    fun validatedProperty() {
        val source = ValueProperty<String?>("abc")

        val validated = Properties.validatedProperty(source,
                object : Predicate<String?> {
                    override fun test(value: String?): Boolean {
                        return if (value == null) false else value.length > 3
                    }
                })

        assertNull(validated.get())

        source.set("aaaaa")
        assertEquals("aaaaa", validated.get())
    }

    @Test
    fun isValidProperty() {
        val source = ValueProperty<String?>("abc")
        val isValid = Properties.isPropertyValid(source,
                object : Predicate<String?> {
                    override fun test(value: String?): Boolean {
                        return if (value == null) false else value.length > 1
                    }
                })
        assertTrue(isValid.get())

        source.set("z")
        assertFalse(isValid.get())
    }
}