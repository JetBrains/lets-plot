package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.function.Predicate
import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.function.Value
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObservableCollectionsTest {
    companion object {

        private val STARTS_WITH_A = object : Predicate<String?> {
            override fun test(value: String?): Boolean {
                return value?.startsWith("a") ?: false
            }
        }
    }

/*
    @Test
    fun testReadingHandlerOnSelectList() {
        val property = ValueProperty<List<String>?>(null)
        val collection = ObservableCollections.selectList(
                property,
//                { value -> toObservable(ArrayList(value)) }
                object : Function<List<String>?, ObservableList<String>> {
                    override fun apply(value: List<String>?): ObservableList<String> {
                        return toObservable(value!!)
                    }
                }
        )

        val registration = collection.addHandler({ event : CollectionItemEvent<String?> -> })
        property.set(listOf("1", "2"))
        registration.dispose()

        collection.addHandler({ event -> })
        assertThat(collection).containsExactlyElementsOf(property.get())
    }
*/

    @Test
    fun count() {
        val collection = ObservableArrayList<String>()
        val count = ObservableCollections.count(collection, STARTS_WITH_A)

        runChanges(collection, count)
    }

    @Test
    fun countListener() {
        val collection = ObservableArrayList<String>()
        val count = ObservableCollections.count(collection, STARTS_WITH_A)

        val lastUpdate: Value<Int> = Value(0)
        count.addHandler(object : EventHandler<PropertyChangeEvent<out Int>> {
            override fun onEvent(event: PropertyChangeEvent<out Int>) {
                lastUpdate.set(event.newValue!!)
            }
        })

        runChanges(collection, lastUpdate)
    }

    @Test
    fun allTest() {
        val collection = ObservableArrayList<String>()
        val all = ObservableCollections.all(collection, STARTS_WITH_A)

        assertTrue(all.get()!!)

        collection.add("a")
        assertTrue(all.get()!!)
        collection.add("b")
        assertFalse(all.get()!!)

        collection.clear()
        assertTrue(all.get()!!)
    }

    @Test
    fun anyTest() {
        val collection = ObservableArrayList<String>()
        val any = ObservableCollections.any(collection, STARTS_WITH_A)

        assertFalse(any.get()!!)

        collection.add("b")
        assertFalse(any.get()!!)
        collection.add("a")
        assertTrue(any.get()!!)

        collection.clear()
        assertFalse(any.get()!!)
    }

    private fun runChanges(collection: ObservableList<String>, count: Supplier<out Int>) {
        assertEquals(0, count.get())

        collection.add("a")
        assertEquals(1, count.get())
        collection.add("b")
        assertEquals(1, count.get())
        collection.add("a")
        assertEquals(2, count.get())
        collection.add("b")
        assertEquals(2, count.get())

        collection.removeAt(1)
        assertEquals(2, count.get())
        collection.removeAt(1)
        assertEquals(1, count.get())

        collection.clear()
        assertEquals(0, count.get())
    }
}