/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.test.Test
import kotlin.test.assertTrue

class DefaultMapperProviderTest {
    @Test
    fun everyAesHasMapperProvider() {
        for (aes in Aes.values()) {
            assertTrue(DefaultMapperProvider.hasDefault(aes), "Aes " + aes.name + " has MapperProvider")
        }
    }
}