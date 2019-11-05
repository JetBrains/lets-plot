/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.gcommon.base

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {

    @Test
    fun repeat() {
        assertEquals("heyheyhey", Strings.repeat("hey", 3))
    }

}
