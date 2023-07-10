/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import jetbrains.datalore.base.function.Supplier
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test

class PropertyTest {
    @Test
    fun incorrectEventFiring() {
        val prop = ValueProperty<String?>(null)

        val derived = SimpleDerivedProperty(object : Supplier<Int> {
            override fun get(): Int {
                val value = prop.get()
                return value?.length ?: 0
            }
        }, prop)

        prop.set("xyz")

        val handler = mock(EventHandler::class.java)

        @Suppress("UNCHECKED_CAST")
        derived.addHandler(handler as EventHandler<PropertyChangeEvent<out Int>>)

        prop.set("")

        verify(handler).onEvent(PropertyChangeEvent(3, 0))
    }

    @Test(expected = IllegalStateException::class)
    fun wrapTooBigCollection() {
        val list = ObservableArrayList<Int>()
        list.add(0)
        list.add(1)
        Properties.forSingleItemCollection(list)
    }
}