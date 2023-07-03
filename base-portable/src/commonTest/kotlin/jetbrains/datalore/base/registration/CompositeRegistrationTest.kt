/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration

import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeRegistrationTest {
    private var myRemoveCounter = 0

    @Test
    fun removalOrder() {
        val r = CompositeRegistration(createReg(1), createReg(0))
        r.remove()
        assertEquals(2, myRemoveCounter)
    }

    @Test
    fun removalOrderManualAdd() {
        val r = CompositeRegistration()
        r.add(createReg(1)).add(createReg(0))
        r.remove()
        assertEquals(2, myRemoveCounter)
    }

    private fun createReg(expectedOrder: Int): Registration {
        return object : Registration() {
            override fun doRemove() {
                assertEquals(expectedOrder, myRemoveCounter++)
            }
        }
    }
}