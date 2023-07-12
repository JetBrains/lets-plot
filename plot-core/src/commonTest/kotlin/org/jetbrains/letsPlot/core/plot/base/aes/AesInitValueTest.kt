/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import kotlin.test.Test
import kotlin.test.assertTrue

class AesInitValueTest {
    @Test
    fun everyAesHasInitValue() {
        for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.values()) {
            assertTrue(AesInitValue.has(aes), "Aes " + aes.name + " has init value")
        }
    }
}