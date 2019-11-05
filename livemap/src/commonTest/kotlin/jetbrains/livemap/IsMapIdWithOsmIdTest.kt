/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.livemap.MapLayerGeocodingHelper.Companion.isMapIdWithOsmId
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsMapIdWithOsmIdTest {

    @Test
    fun fooTest() {
        val value0 = "123"
        val value1 = "abc"
        val value2 = ""
        val value3 = null
        val value4 = "12c"

        assertTrue { isMapIdWithOsmId(value0) }
        assertFalse { isMapIdWithOsmId(value1) }
        assertFalse { isMapIdWithOsmId(value2) }
        assertFalse { isMapIdWithOsmId(value3) }
        assertFalse { isMapIdWithOsmId(value4) }
    }
}