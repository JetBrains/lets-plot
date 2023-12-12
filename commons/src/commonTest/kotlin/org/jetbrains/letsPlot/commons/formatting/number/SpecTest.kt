/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class SpecTest {
    @Test
    fun doubleNormalizationShouldNotAlterSpec() {
        // This test mimics NumberFormat ctor as it implicitly normalizes spec.
        val defaultSpec = NumberFormat.Spec()
        val norm = NumberFormat.normalizeSpec(defaultSpec)
        val norm2 = NumberFormat.normalizeSpec(norm)

        assertEquals(norm, norm2)
    }
}
