package jetbrains.datalore.visualization.plot.base.aes

import jetbrains.datalore.visualization.plot.base.render.Aes
import kotlin.test.Test
import kotlin.test.assertTrue

class AesInitValueTest {
    @Test
    fun everyAesHasInitValue() {
        for (aes in Aes.values()) {
            assertTrue(AesInitValue.has(aes), "Aes " + aes.name + " has init value")
        }
    }
}