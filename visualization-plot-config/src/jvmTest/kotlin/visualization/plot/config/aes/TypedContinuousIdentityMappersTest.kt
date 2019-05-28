package jetbrains.datalore.visualization.plot.config.aes

import jetbrains.datalore.base.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class TypedContinuousIdentityMappersTest {
    private fun assertColor(expected: Color, value: Double?) {
        assertEquals(expected, TypedContinuousIdentityMappers.COLOR(value))
    }

    @Test
    fun colorValues() {
        assertColor(Color.RED, 0xff0000.toDouble())
        assertColor(Color.GREEN, 0x00ff00.toDouble())
        assertColor(Color.BLUE, 0x0000ff.toDouble())
        assertColor(Color.BLACK, 0.0)
        assertColor(Color.WHITE, 0xffffff.toDouble())
    }

    @Test
    fun colorValuesNeg() {
        assertColor(Color.RED, (-0xff0000).toDouble())
        assertColor(Color.GREEN, (-0x00ff00).toDouble())
        assertColor(Color.BLUE, (-0x0000ff).toDouble())
        assertColor(Color.BLACK, -0.0)
        assertColor(Color.WHITE, (-0xffffff).toDouble())
    }
}