/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType.E
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.core.commons.data.DataType
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterUtilTest {

    @Test
    fun intDtype() {
        assertEquals("1", formatDType(1.0, DataType.INTEGER))
        assertEquals("2", formatDType(1.5, DataType.INTEGER))
        assertEquals("1000000000000000000000000000000", formatDType(1e30, DataType.INTEGER))
    }

    @Test
    fun floatingDtype() {
        assertEquals("1", formatDType(1, DataType.FLOATING))
        assertEquals("1", formatDType(1.0, DataType.FLOATING))
        assertEquals("1.5", formatDType(1.5, DataType.FLOATING))
        assertEquals("1e+30", formatDType(1e30, DataType.FLOATING))
    }

    @Test
    fun unknownDtype() {
        assertEquals("1", formatDType(1.0, DataType.UNKNOWN))
        assertEquals("1", formatDType(1, DataType.UNKNOWN))
        assertEquals("1.5", formatDType(1.5, DataType.UNKNOWN))
        assertEquals("1e+30", formatDType(1e30, DataType.UNKNOWN))
        assertEquals("asd", formatDType("asd", DataType.UNKNOWN))
    }

    private fun formatDType(value: Any, dataType: DataType): String {
        return FormatterUtil.byDataType(dataType, ExponentFormat(E))(value)
    }
}
