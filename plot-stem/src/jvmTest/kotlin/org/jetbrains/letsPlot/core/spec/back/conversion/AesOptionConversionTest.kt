/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config.conversion

import demoAndTestShared.assertDoesNotFail
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import kotlin.test.Test

class AesOptionConversionTest {
    @Test
    fun everyAesHasOptionValueConverter() {
        for (aes in Aes.values()) {
            assertDoesNotFail("Aes '${aes.name}' has no option value converter. ") {
                AesOptionConversion.demoAndTest.getConverter(aes)
            }
        }
    }
}