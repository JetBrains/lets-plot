package org.jetbrains.letsPlot.commons.event


import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MouseEventPeerTest {

    @Test
    fun `events dispatched manually should reach handlers`() {
        val peer = MouseEventPeer()
        var eventReceived = false

        peer.addEventHandler(MouseEventSpec.MOUSE_PRESSED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                eventReceived = true
            }
        })

        peer.dispatch(MouseEventSpec.MOUSE_PRESSED, MouseEvent(0, 0, Button.LEFT, KeyModifiers.emptyModifiers()))

        assertTrue(eventReceived, "Handler should receive manually dispatched event")
    }

    @Test
    fun `events from added source should propagate to peer handlers`() {
        val peer = MouseEventPeer()
        val mockSource = MockEventSource()

        // 1. Connect source
        peer.addEventSource(mockSource)

        var eventReceived = false
        // 2. Add handler to peer
        peer.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                eventReceived = true
            }
        })

        // 3. Fire event from mock source
        mockSource.fire(MouseEventSpec.MOUSE_CLICKED, MouseEvent(10, 10, Button.LEFT, KeyModifiers.emptyModifiers()))

        assertTrue(eventReceived, "Event from upstream source should propagate to peer")
    }

    @Test
    fun `adding handler to peer should subscribe to existing sources`() {
        val peer = MouseEventPeer()
        val mockSource = MockEventSource()

        peer.addEventSource(mockSource)
        assertEquals(0, mockSource.listenerCount(MouseEventSpec.MOUSE_PRESSED), "No subscription yet")

        // Add handler -> peer should now subscribe to source
        val handlerReg = peer.addEventHandler(MouseEventSpec.MOUSE_PRESSED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {}
        })

        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_PRESSED), "Peer should subscribe to source when handler is added")

        // Cleanup
        handlerReg.remove()
        assertEquals(0, mockSource.listenerCount(MouseEventSpec.MOUSE_PRESSED), "Peer should unsubscribe from source when handler is removed")
    }

    @Test
    fun `adding source should immediately subscribe if peer has handlers`() {
        val peer = MouseEventPeer()

        // Peer already has a handler
        peer.addEventHandler(MouseEventSpec.MOUSE_MOVED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {}
        })

        val mockSource = MockEventSource()

        // Add source
        peer.addEventSource(mockSource)

        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_MOVED), "Peer should immediately subscribe to new source for existing specs")
    }

    @Test
    fun `removing event source (Memory Leak Fix) should disconnect listeners`() {
        val peer = MouseEventPeer()
        val mockSource = MockEventSource()

        // 1. Setup: Peer has interest in MOUSE_PRESSED
        peer.addEventHandler(MouseEventSpec.MOUSE_PRESSED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {}
        })

        // 2. Add source and verify connection
        val sourceReg = peer.addEventSource(mockSource)
        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_PRESSED), "Source should be connected")

        // 3. REMOVE source
        sourceReg.remove()

        // 4. Verify disconnection
        assertEquals(0, mockSource.listenerCount(MouseEventSpec.MOUSE_PRESSED), "Removing source registration should unsubscribe peer from source")
    }

    @Test
    fun `removing last handler should unsubscribe from all sources`() {
        val peer = MouseEventPeer()
        val mockSource1 = MockEventSource()
        val mockSource2 = MockEventSource()

        peer.addEventSource(mockSource1)
        peer.addEventSource(mockSource2)

        val reg = peer.addEventHandler(MouseEventSpec.MOUSE_ENTERED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {}
        })

        assertEquals(1, mockSource1.listenerCount(MouseEventSpec.MOUSE_ENTERED))
        assertEquals(1, mockSource2.listenerCount(MouseEventSpec.MOUSE_ENTERED))

        // Remove the only handler
        reg.remove()

        assertEquals(0, mockSource1.listenerCount(MouseEventSpec.MOUSE_ENTERED), "Should unsubscribe from source 1")
        assertEquals(0, mockSource2.listenerCount(MouseEventSpec.MOUSE_ENTERED), "Should unsubscribe from source 2")
    }

    @Test
    fun `multiple event specs should be handled independently`() {
        val peer = MouseEventPeer()
        val mockSource = MockEventSource()
        peer.addEventSource(mockSource)

        val regClick = peer.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> { override fun onEvent(event: MouseEvent) {} })
        val regMove = peer.addEventHandler(MouseEventSpec.MOUSE_MOVED, object : EventHandler<MouseEvent> { override fun onEvent(event: MouseEvent) {} })

        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_CLICKED))
        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_MOVED))

        // Remove one
        regClick.remove()

        assertEquals(0, mockSource.listenerCount(MouseEventSpec.MOUSE_CLICKED), "Should unsubscribe clicked")
        assertEquals(1, mockSource.listenerCount(MouseEventSpec.MOUSE_MOVED), "Should keep moved")
    }

    // --- Helper Mock Class ---

    class MockEventSource : MouseEventSource {
        private val listeners = HashMap<MouseEventSpec, MutableList<EventHandler<MouseEvent>>>()

        override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
            listeners.getOrPut(eventSpec) { ArrayList() }.add(eventHandler)

            return object : Registration() {
                override fun doRemove() {
                    listeners[eventSpec]?.remove(eventHandler)
                }
            }
        }

        fun fire(spec: MouseEventSpec, event: MouseEvent) {
            listeners[spec]?.toList()?.forEach { it.onEvent(event) }
        }

        fun listenerCount(spec: MouseEventSpec): Int {
            return listeners[spec]?.size ?: 0
        }
    }
}