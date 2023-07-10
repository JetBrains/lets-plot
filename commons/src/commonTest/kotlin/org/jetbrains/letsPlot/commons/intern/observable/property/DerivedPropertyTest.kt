/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import jetbrains.datalore.base.function.Value
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DerivedPropertyTest {
    private val string = ValueProperty("a")
    private val length = object : DerivedProperty<Int>(0, string) {
        override fun doGet(): Int {
            return string.get().length
        }
    }

    @Test
    fun propertyWithoutHandlers() {
        assertEquals(1, length.get())
        string.set("aa")
        assertEquals(2, length.get())
    }

    @Test
    fun handlerAddThenRemoved() {
        val reg = length.addHandler(object : EventHandler<PropertyChangeEvent<out Int>> {
            override fun onEvent(event: PropertyChangeEvent<out Int>) {}
        })
        reg.remove()

        string.set("aa")
        assertEquals(2, length.get())
    }

    @Test
    fun getBeforeEventReturnsOldValue() {
        val lengthValue = Value(0)
        val lengthEventFired = Value(false)

        string.addHandler(object : EventHandler<PropertyChangeEvent<out String>> {
            override fun onEvent(event: PropertyChangeEvent<out String>) {
                assertFalse(lengthEventFired.get())
                lengthValue.set(length.get())
            }
        })
        length.addHandler(object : EventHandler<PropertyChangeEvent<out Int>> {
            override fun onEvent(event: PropertyChangeEvent<out Int>) {
                lengthEventFired.set(true)
            }
        })

        string.set("aa")
        assertTrue(lengthEventFired.get())
        assertEquals(1, lengthValue.get())
        assertEquals(2, length.get())
    }
}