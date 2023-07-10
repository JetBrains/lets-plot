/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertySelectionTest {
    private val c2 = C2()
    private val selProp = Properties.select(c2.ref, { value -> value!!.value }, 30)
    private var changed = false

    private fun addListener() {
        selProp.addHandler(object : EventHandler<PropertyChangeEvent<out Int?>> {
            override fun onEvent(event: PropertyChangeEvent<out Int?>) {
                changed = true
            }
        })
    }

    @Test
    fun initialValue() {
        assertEquals(30, selProp.get() as Int)
    }

    @Test
    fun valueSet() {
        addListener()
        val c1 = C1(239)
        c2.ref.set(c1)
        assertEquals(239, selProp.get() as Int)
        assertTrue(changed)
    }

    @Test
    fun subvalueChange() {
        addListener()
        val c1 = C1(239)
        c2.ref.set(c1)

        changed = false
        c1.value.set(30)
        assertTrue(changed)
    }

    @Test
    fun subvalueChangeListenerAddedAfterRefSet() {
        val c1 = C1(239)
        c2.ref.set(c1)
        addListener()

        changed = false
        c1.value.set(30)
        assertTrue(changed)
    }

    internal class C1(v: Int?) {
        internal val value = ValueProperty<Int?>(null)

        init {
            value.set(v)
        }
    }

    internal class C2 {
        internal val ref = ValueProperty<C1?>(null)
    }
}