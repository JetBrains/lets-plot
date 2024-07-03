/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.platf.dom

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEventInit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomMouseEventMapperTest {
    @Test
    fun wholeTarget() {
        val div = EventTargetAdapter(600, 600)
        val m = div.createEventMapper()

        assertEvent(MOUSE_ENTERED, 0, 300, div.dispatchEvent("mouseenter", 0, 300)[m]!!.single())
    }

    @Test
    fun singleDivWithTwoEventAreas() {
        val targetAdapter = EventTargetAdapter(1200, 600)

        val leftArea = targetAdapter.createEventMapper(DoubleRectangle.XYWH(0, 0, 599, 600))
        val rightArea = targetAdapter.createEventMapper(DoubleRectangle.XYWH(600, 0, 600, 600))

        targetAdapter.dispatchEvent("mouseenter", 0, 300).let { events ->
            assertEvent(MOUSE_ENTERED, 0, 300, events[leftArea]!!.single())
            assertNoEvents(rightArea, events)
        }

        targetAdapter.dispatchEvent("mousemove", 300, 300).let { events ->
            assertEvent(MOUSE_MOVED, 300, 300, events[leftArea]!!.single())
            assertNoEvents(rightArea, events)
        }

        targetAdapter.dispatchEvent("mousemove", 600, 300).let { events ->
            printEvents(events)
            assertEvent(MOUSE_LEFT, 600, 300, events[leftArea]!!.single())
            assertEvent(MOUSE_ENTERED, 0, 300, events[rightArea]!!.single())
        }

        targetAdapter.dispatchEvent("mousemove", 601, 300).let { events ->
            printEvents(events)
            assertNoEvents(leftArea, events)
            assertEvent(MOUSE_MOVED, 1, 300, events[rightArea]!!.single())
        }

        targetAdapter.dispatchEvent("mousemove", 650, 300).let { events ->
            assertNoEvents(leftArea, events)
            assertEvent(MOUSE_MOVED, 50, 300, events[rightArea]!!.single())
        }
    }

    private fun assertEvent(
        expectedSpec: MouseEventSpec,
        expectedX: Int,
        expectedY: Int,
        actualEvent: Pair<MouseEventSpec, MouseEvent>
    ) {
        val (spec, event) = actualEvent
        // Triple is used to provide more informative error message - it shows all three values together
        assertEquals(Triple(expectedSpec, expectedX, expectedY), Triple(spec, event.x, event.y))
    }

    private fun assertNoEvents(id: DomMouseEventMapper, events: Map<DomMouseEventMapper, List<Pair<MouseEventSpec, MouseEvent>>>) {
        val msg = events[id]?.joinToString { (spec, event) -> "[$spec at (${event.x}, ${event.y})]" } ?: ""
        assertTrue(msg) { id !in events }
    }

    private fun printEvents(events: Map<DomMouseEventMapper, List<Pair<MouseEventSpec, MouseEvent>>>) {
        events.forEach { (id, list) ->
            println("Events for $id:")
            list.forEach { (spec, event) ->
                println("  $spec at (${event.x}, ${event.y})")
            }
        }
    }

    private class EventTargetAdapter(
        private val w: Int,
        private val h: Int
    ) {
        val target = kotlinx.browser.document.createElement("div") as HTMLDivElement
        private val mappers = mutableListOf<DomMouseEventMapper>()
        private val events = mutableMapOf<DomMouseEventMapper, MutableList<Pair<MouseEventSpec, MouseEvent>>>()
        private val regs = CompositeRegistration()

        init {
            target.style.width = "${w}px"
            target.style.height = "${h}px"
        }

        fun createEventMapper(r: DoubleRectangle? = null): DomMouseEventMapper {
            val area = r ?: DoubleRectangle.XYWH(0, 0, w, h)
            val eventMapper = DomMouseEventMapper(target, eventArea = area)

            fun log(spec: MouseEventSpec) {
                eventMapper
                    .on(spec) { events.getOrPut(eventMapper) { mutableListOf() } += spec to it }
                    .also(regs::add)
            }

            log(MOUSE_ENTERED)
            log(MOUSE_LEFT)
            log(MOUSE_MOVED)

            mappers.add(eventMapper)

            return eventMapper
        }

        fun dispatchEvent(
            type: String,
            x: Number,
            y: Number
        ): Map<DomMouseEventMapper, List<Pair<MouseEventSpec, MouseEvent>>> {
            val event = org.w3c.dom.events.MouseEvent(type, object : MouseEventInit {
                override var clientX: Int? = x.toInt()
                override var clientY: Int? = y.toInt()
            })

            events.clear()
            target.dispatchEvent(event)
            return events.toMap() // copy
        }
    }

}