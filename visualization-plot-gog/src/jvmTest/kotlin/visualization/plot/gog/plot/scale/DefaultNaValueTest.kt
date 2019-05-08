package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import kotlin.test.Test
import kotlin.test.assertTrue

class DefaultNaValueTest {
    @Test
    fun everyAesHasNaValue() {
        for (aes in Aes.values()) {
            assertTrue(DefaultNaValue.has(aes), "Aes " + aes.name + " has n/a value")
        }
    }
}