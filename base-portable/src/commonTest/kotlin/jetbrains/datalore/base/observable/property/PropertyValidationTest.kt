/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import kotlin.test.*

class PropertyValidationTest {
    @Test
    fun validatedProperty() {
        val source = ValueProperty<String?>("abc")

        val validated = Properties.validatedProperty(source) { value -> if (value == null) false else value.length > 3 }

        assertNull(validated.get())

        source.set("aaaaa")
        assertEquals("aaaaa", validated.get())
    }

    @Test
    fun isValidProperty() {
        val source = ValueProperty<String?>("abc")
        val isValid = Properties.isPropertyValid(source) { value -> if (value == null) false else value.length > 1 }
        assertTrue(isValid.get())

        source.set("z")
        assertFalse(isValid.get())
    }
}