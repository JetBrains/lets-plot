/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.SimpleEventSource
import org.jetbrains.letsPlot.commons.registration.Registration
import org.mockito.Mockito
import kotlin.test.BeforeTest
import kotlin.test.Test

class EventSelectionTest {
    private val es1 = SimpleEventSource<Any?>()
    private val es2 = SimpleEventSource<Any?>()
    private val prop = ValueProperty(false)

    private val result = Properties.selectEvent(prop) { source -> if (source) es1 else es2 }

    @Suppress("UNCHECKED_CAST")
    private val handler: EventHandler<Any?> = Mockito.mock(EventHandler::class.java) as EventHandler<Any?>
    private var reg: Registration? = null

    @BeforeTest
    fun before() {
        reg = result.addHandler(handler)
    }


    @Test
    fun ignoredEvent() {
        es1.fire(null)

        assertFired()
    }


    @Test
    fun event() {
        es2.fire(null)

        assertFired(null)
    }

    @Test
    fun switchEvents() {
        es1.fire("a")
        es2.fire("b")

        prop.set(true)
        es2.fire("c")
        es1.fire("d")

        assertFired("b", "d")
    }

    @Test
    fun unregister() {
        reg!!.remove()

        assertFired()
    }

    private fun assertFired(vararg items: Any?) {
        for (s in items) {
            Mockito.verify(handler).onEvent(s)
        }
        Mockito.verifyNoMoreInteractions(handler)
    }
}