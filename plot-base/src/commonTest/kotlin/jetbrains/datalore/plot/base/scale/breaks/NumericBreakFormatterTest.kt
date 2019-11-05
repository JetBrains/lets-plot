/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBreakFormatterTest {
    @Test
    fun formatZero() {
        val formatter = NumericBreakFormatter(0.0, 0.0, true)
        assertEquals("0", formatter.apply(0), "format 0")
    }

}