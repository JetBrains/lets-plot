/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
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