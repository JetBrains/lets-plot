package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.event.EventHandler
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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