/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transf

import org.jetbrains.letsPlot.core.spec.transf.SpecSelector
import kotlin.test.Test
import kotlin.test.assertEquals

class SpecSelectorTest {
    @Test
    fun equalCreated() {
        val set = setOf(
                SpecSelector.of("common0", "common1"),
                SpecSelector.of("common0", "common1")
        )

        assertEquals(1, set.size)
    }

    @Test
    fun unequalCreated() {
        val set = setOf(
                SpecSelector.of("common0", "different1"),
                SpecSelector.of("common0", "different2")
        )

        assertEquals(2, set.size)
    }

    @Test
    fun equalCreatedAndGrown() {
        val set = setOf(
                SpecSelector.of("common0", "common1"),
                SpecSelector.root()
                        .with().part("common0").build()
                        .with().part("common1").build()
        )

        if (set.size > 1) {
            val it = set.iterator()
            assertEquals(
                    it.next(),
                    it.next()
            )
        }
    }

}