/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.enums

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import org.jetbrains.letsPlot.core.commons.enums.EnumInfoTest.Scope.*
import kotlin.test.*

class EnumInfoTest {

    @Test
    fun test() {
        checkValid(TEST, "TEST")
    }

    @Test
    fun lower() {
        checkValid(TEST, "test")
    }

    @Test
    fun invalid() {
        checkInvalid("method")
    }

    @Test
    fun nullCase() {
        checkInvalid(null)
    }

    @Test
    fun empty() {
        checkInvalid("")
    }

    @Test
    fun suiteCase() {
        checkValid(SUITE, "suite")
    }

    @Test
    fun classCase() {
        checkValid(CLASS, "class")
    }

    @Test
    fun duplicateValues() {
        assertFailsWith<IllegalArgumentException> {
            checkEnumConstant(EnumWithDuplicates.TEST, "TEST", "TEST")
            checkEnumConstant(EnumWithDuplicates.CLASS, "CLASS", "test")
            EnumInfoFactory.createEnumInfo<EnumWithDuplicates>()
        }
    }

    @Test
    fun unsafeValueOf() {
        assertFailsWith<IllegalArgumentException> {
            Scope.unsafeValueOf("method")
        }
    }

    @Test
    fun originalNames() {
        assertEquals(listOf("TEST", "CLASS", "SUITE"), Scope.originalNames)
    }

    private fun checkValid(expected: Scope, text: String) {
        assertTrue(Scope.hasValue(text))
        assertEquals(expected, Scope.unsafeValueOf(text))
        assertEquals(expected, Scope.safeValueOf(text))
        assertEquals(expected, Scope.safeValueOf(text, SUITE))
    }

    private fun checkInvalid(text: String?) {
        assertFalse(Scope.hasValue(text))
        assertNull(Scope.safeValueOf(text))
        assertEquals(SUITE, Scope.safeValueOf(text, SUITE))
    }

    private fun <EnumT : Enum<EnumT>> checkEnumConstant(enumConstant: EnumT, expectedName: String, expectedString: String) {
        assertEquals(expectedName, enumConstant.name)
        assertEquals(expectedString, enumConstant.toString())
    }

    internal enum class Scope {

        TEST, CLASS, SUITE;

        companion object {
            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Scope>()

            internal fun hasValue(text: String?): Boolean {
                return ENUM_INFO.hasValue(text)
            }

            internal fun unsafeValueOf(text: String): Scope {
                return ENUM_INFO.unsafeValueOf(text)
            }

            internal fun safeValueOf(text: String?): Scope? {
                return ENUM_INFO.safeValueOf(text)
            }

            internal fun safeValueOf(text: String?, defaultScope: Scope): Scope {
                return ENUM_INFO.safeValueOf(text, defaultScope)
            }

            internal val originalNames: List<String>
                get() = ENUM_INFO.originalNames
        }
    }

    internal enum class EnumWithDuplicates {
        TEST,
        CLASS {
            override fun toString(): String {
                return "test"
            }
        }
    }
}
