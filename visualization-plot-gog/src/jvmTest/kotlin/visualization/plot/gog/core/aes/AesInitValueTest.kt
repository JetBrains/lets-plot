package jetbrains.datalore.visualization.plot.gog.core.aes

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import org.junit.Assert
import org.junit.Test

class AesInitValueTest {
    @Test
    fun everyAesHasInitValue() {
        for (aes in Aes.values()) {
            Assert.assertTrue("Aes " + aes.name() + " has init value", AesInitValue.has(aes))
        }
    }
}