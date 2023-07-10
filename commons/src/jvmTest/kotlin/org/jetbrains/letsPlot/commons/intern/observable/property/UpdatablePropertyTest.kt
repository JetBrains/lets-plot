/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.mockito.Mockito
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdatablePropertyTest {
    private var value: String? = null
    private var property: UpdatableProperty<String?>? = null

    @BeforeTest
    fun init() {
        property = object : UpdatableProperty<String?>() {
            override fun doGet(): String? {
                return value
            }

            override fun doAddListeners() {}

            override fun doRemoveListeners() {}
        }
    }

    @Test
    fun simpleGet() {
        value = "z"

        assertEquals("z", property!!.get())
    }

    @Test
    fun getWithListenersDoesntGetWithoutUpdate() {
        value = "a"
        val handler = Mockito.mock(EventHandler::class.java)
        @Suppress("UNCHECKED_CAST")
        property!!.addHandler(handler as EventHandler<PropertyChangeEvent<out String?>>)
        value = "b"

        assertEquals("a", property!!.get())
    }

    @Test
    fun updateFiresEvent() {
        val handler = Mockito.mock(EventHandler::class.java)
        @Suppress("UNCHECKED_CAST")
        property!!.addHandler(handler as EventHandler<PropertyChangeEvent<out String?>>)
        value = "z"

        property!!.update()

        Mockito.verify(handler).onEvent(PropertyChangeEvent(null, "z"))
    }

    @Test
    fun updateWithoutChangeDoesntFireEvent() {
        val handler = Mockito.mock(EventHandler::class.java)
        @Suppress("UNCHECKED_CAST")
        property!!.addHandler(handler as EventHandler<PropertyChangeEvent<out String?>>)

        property!!.update()

        Mockito.verifyNoMoreInteractions(handler)

    }

    @Test
    fun removeAllListenersReturnsToSimpleMode() {
        val handler = Mockito.mock(EventHandler::class.java)
        @Suppress("UNCHECKED_CAST")
        property!!.addHandler(handler as EventHandler<PropertyChangeEvent<out String?>>).remove()

        value = "c"

        assertEquals("c", property!!.get())
    }

}