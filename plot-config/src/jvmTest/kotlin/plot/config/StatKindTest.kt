/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import kotlin.test.Test
import kotlin.test.assertEquals

class StatKindTest {

    @Test
    fun valueOf() {
        assertEquals(StatKind.COUNT, StatKind.safeValueOf("count"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun unknownName() {
        StatKind.safeValueOf("coun")
    }
}
