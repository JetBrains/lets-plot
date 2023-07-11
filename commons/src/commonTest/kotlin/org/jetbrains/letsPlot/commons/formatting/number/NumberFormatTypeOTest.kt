/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeOTest {
    @Test
    fun octal() {
        assertEquals("12", NumberFormat("o").apply(10))
    }

    @Test
    fun octalWithPrefix() {
        assertEquals("0o12", NumberFormat("#o").apply(10))
    }
}