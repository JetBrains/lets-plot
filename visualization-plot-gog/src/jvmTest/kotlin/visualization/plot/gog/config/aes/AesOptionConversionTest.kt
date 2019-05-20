package jetbrains.datalore.visualization.plot.gog.config.aes

import jetbrains.datalore.visualization.plot.base.render.Aes
import kotlin.test.Test
import kotlin.test.assertTrue

class AesOptionConversionTest {
    @Test
    fun everyAesHasOptionValueConverter() {
        for (aes in Aes.values()) {
            assertTrue(AesOptionConversion.has(aes), "Aes ${aes.name} has option value converter")
        }
    }
}