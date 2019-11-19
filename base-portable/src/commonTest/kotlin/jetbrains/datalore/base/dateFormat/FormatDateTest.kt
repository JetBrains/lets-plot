/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.dateFormat

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatDateTest {
    private val date = Date(6, Month.AUGUST, 2019)

    @Test
    fun onlyDate() {
        val f = Format("%Y-%m-%dT%H:%M:%S")
        assertEquals("2019-08-06T::", f.apply(date))
    }
}