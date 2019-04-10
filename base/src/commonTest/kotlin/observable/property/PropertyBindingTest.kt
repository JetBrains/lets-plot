package jetbrains.datalore.base.observable.property

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyBindingTest {
    private val source = ValueProperty<String?>(null)
    private val target = ValueProperty<String?>(null)

    @Test
    fun bidirectionalSync() {
        source.set("239")
        val reg = PropertyBinding.bindTwoWay(source, target)

        assertEquals("239", target.get())

        target.set("z")
        assertEquals("z", source.get())

        reg.remove()
        source.set("zzz")

        assertEquals("z", target.get())
    }
}