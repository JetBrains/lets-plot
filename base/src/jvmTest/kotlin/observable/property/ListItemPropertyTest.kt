package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.event.testCase.EventMatchers
import jetbrains.datalore.base.event.testCase.EventMatchers.allEvents
import jetbrains.datalore.base.event.testCase.EventMatchers.newValue
import jetbrains.datalore.base.event.testCase.EventMatchers.newValueIs
import jetbrains.datalore.base.event.testCase.EventMatchers.noEvents
import jetbrains.datalore.base.event.testCase.EventMatchers.oldValueIs
import jetbrains.datalore.base.event.testCase.EventMatchers.setTestHandler
import jetbrains.datalore.base.event.testCase.EventMatchers.singleEvent
import jetbrains.datalore.base.observable.collections.DataloreIndexOutOfBoundsException
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ListItemPropertyTest {

    @JvmField
    @Rule
    val exception = ExpectedException.none()

    private val list = createList(5)
    private val p1 = ListItemProperty(list, 1)
    private val p2 = ListItemProperty(list, 2)
    private val p3 = ListItemProperty(list, 3)

    private val p1Handler = setTestHandler(p1)
    private val p2Handler = setTestHandler(p2)
    private val p3Handler = setTestHandler(p3)

    private val p1indexHandler = setTestHandler(p1.index)
    private val p2indexHandler = setTestHandler(p2.index)
    private val p3indexHandler = setTestHandler(p3.index)

    @Test
    fun rejectsNegativeIndex() {
        val list = createList(5)
        exception.expect(DataloreIndexOutOfBoundsException::class.java)
        ListItemProperty(list, -1)
    }

    @Test
    fun rejectsTooSmallIndex() {
        val list = ObservableArrayList<Int?>()
        exception.expect(DataloreIndexOutOfBoundsException::class.java)
        ListItemProperty(list, 0)
    }

    @Test
    fun rejectsTooLargeIndex() {
        val list = createList(5)
        exception.expect(DataloreIndexOutOfBoundsException::class.java)
        ListItemProperty(list, 5)
    }

    @Test
    fun acceptsEdgeIndices() {
        val list = createList(5)
        ListItemProperty(list, 0)
        ListItemProperty(list, 4)
    }

    @Test
    fun getsTheRightItem() {
        val list = createList(5)
        val p2 = ListItemProperty(list, 2)
        assertEquals(2, p2.get()!!.toInt())

        val p4 = ListItemProperty(list, 4)
        assertEquals(4, p4.get()!!.toInt())
    }

    @Test
    fun setsTheRightItem() {
        val list = createList(5)
        val p2 = ListItemProperty(list, 2)
        p2.set(12)
        assertEquals("[0, 1, 12, 3, 4]", "" + list)

        val p4 = ListItemProperty(list, 4)
        p4.set(14)
        assertEquals("[0, 1, 12, 3, 14]", "" + list)
    }

    @Test
    fun tracksItemOnAdd() {
        list.add(2, 22)
        assertEquals(1, p1.get()!!.toInt())
        assertEquals(2, p2.get()!!.toInt())
        assertEquals(3, p3.get()!!.toInt())

        p1.set(11)
        p2.set(12)
        p3.set(13)
        assertEquals("[0, 11, 22, 12, 13, 4]", "" + list)
    }

    @Test
    fun tracksItemOnRemove() {
        list.removeAt(2)
        assertEquals(1, p1.get()!!.toInt())
        assertEquals(3, p3.get()!!.toInt())
        assertFalse(p2.isValid)

        p1.set(11)
        p3.set(13)
        assertEquals("[0, 11, 13, 4]", "" + list)

        exception.expect(IllegalStateException::class.java)
        p2.set(12)
    }

    @Test
    fun firesOnListSet() {
        list.add(2, 22)
        list[3] = 12

        assertThat(p1Handler, noEvents())
        val singleEvent: Matcher<EventMatchers.MatchingHandler<PropertyChangeEvent<out Int?>>> = singleEvent(
                allOf(oldValueIs(2), newValueIs(12)) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p2Handler, singleEvent)
        assertThat(p3Handler, noEvents())
    }

    @Test
    fun firesOnTrackedItemRemove() {
        list.removeAt(2)

        val singleEvent = singleEvent(
                allOf(oldValueIs(2), newValue(nullValue(Int::class.java))) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p2indexHandler, singleEvent)
    }

    @Test
    fun firesOnPropertySet() {
        val list = createList(5)
        val p2 = ListItemProperty(list, 2)

        val p2handler = setTestHandler(p2)

        p2.set(12)

        val singleEvent = singleEvent(
                allOf(oldValueIs(2), newValueIs(12)) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p2handler, singleEvent)
    }

    @Test
    fun indexFiresOnListAdd() {
        list.add(2, 22)

        assertThat(p1indexHandler, noEvents())
        val singleEvent = singleEvent(
                allOf(oldValueIs(2), newValueIs(3)) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p2indexHandler, singleEvent)
    }

    @Test
    fun indexFiresOnListRemove() {
        list.removeAt(2)

        assertThat(p1indexHandler, noEvents())
        val singleEvent = singleEvent(
                allOf(oldValueIs(2), newValue(nullValue(Int::class.java))) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p2indexHandler, singleEvent)
        val singleEvent1 = singleEvent(
                allOf(oldValueIs(3), newValueIs(2)) as Matcher<PropertyChangeEvent<out Int?>>)
        assertThat(p3indexHandler, singleEvent1)
    }

    @Test
    fun indexFiresNotOnListSet() {
        list[2] = 22

        assertThat(p1indexHandler, noEvents())
        assertThat(p2indexHandler, noEvents())
        assertThat(p3indexHandler, noEvents())
    }

    @Test
    fun disposeImmediately() {
        p1.dispose()
        exception.expect(IllegalStateException::class.java)
        p1.dispose()
    }

    @Test
    fun disposeInvalid() {
        list.removeAt(1)

        assertFalse(p1.isValid)
        p1.dispose()
        exception.expect(IllegalStateException::class.java)
        p1.dispose()
    }

    @Test
    fun indexFiresNotOnDispose() {
        p1.dispose()
        assertThat(p1indexHandler, noEvents())
    }

    @Test
    fun indexFiresNotOnDisposeInvalid() {
        list.removeAt(1)

        assertThat(p1indexHandler, allEvents(hasSize(1)))
        p1.dispose()
        assertThat(p1indexHandler, allEvents(hasSize(1)))
    }


    private fun createList(n: Int): ObservableList<Int?> {
        val list = ObservableArrayList<Int?>()
        for (i in 0 until n) {
            list.add(i)
        }
        return list
    }
}
