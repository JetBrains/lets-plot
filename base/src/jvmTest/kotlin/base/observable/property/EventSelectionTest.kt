package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.SimpleEventSource
import jetbrains.datalore.base.registration.Registration
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EventSelectionTest {
    private val es1 = SimpleEventSource<Any?>()
    private val es2 = SimpleEventSource<Any?>()
    private val prop = ValueProperty(false)

    private val result = Properties.selectEvent(prop) { source -> if (source) es1 else es2 }

    private val handler: EventHandler<Any?> = Mockito.mock(EventHandler::class.java) as EventHandler<Any?>
    private var reg: Registration? = null

    @Before
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