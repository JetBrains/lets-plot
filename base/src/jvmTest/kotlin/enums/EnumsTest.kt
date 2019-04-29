package jetbrains.datalore.base.enums

import junit.framework.TestCase.assertEquals
import org.junit.Test

class EnumsTest {

    @Test
    fun enumParsing() {
        assertEquals(TestEnum.A, Enums.valueOf(TestEnum::class.java, "aaa"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun illegalArgument() {
        Enums.valueOf(TestEnum::class.java, "A")
    }

    internal enum class TestEnum {
        A {
            override fun toString(): String {
                return "aaa"
            }
        },

        B {
            override fun toString(): String {
                return "bbb"
            }
        }
    }

}
