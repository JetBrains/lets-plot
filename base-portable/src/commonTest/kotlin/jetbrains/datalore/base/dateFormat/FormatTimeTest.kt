/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.dateFormat

import jetbrains.datalore.base.datetime.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTimeTest {
    private val time = Time(4, 46, 35)

    @Test
    fun onlyTime() {
        val f = DateTimeFormat("%Y-%m-%dT%H:%M:%S")
        assertEquals("--T04:46:35", f.apply(time))
    }
}