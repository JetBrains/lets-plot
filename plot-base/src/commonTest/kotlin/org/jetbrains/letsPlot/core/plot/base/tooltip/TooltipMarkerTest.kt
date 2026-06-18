/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TooltipMarkerTest {
    @Test
    fun `marker can have minor color only`() {
        val marker = TooltipMarker.create(majorColor = null, minorColor = Color.BLUE)

        assertNull(marker.majorColor)
        assertEquals(Color.BLUE, marker.minorColor)
    }
}
