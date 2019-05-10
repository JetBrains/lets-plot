package jetbrains.datalore.visualization.plot.gog.config.transform

import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import org.junit.Rule
import org.junit.Test.None
import org.junit.rules.ExpectedException
import kotlin.test.Test
import kotlin.test.assertEquals

class PlotSpecTransformToMutableTest {
    @get:Rule
    var exception = ExpectedException.none()!!

    @Test
    fun toMutable() {
        val im = mutableMapOf(
                "a" to 0,
                "b" to listOf(0, 0),
                "c" to mapOf("c_" to 0),
                "d" to listOf(mapOf("d0_" to 0))
        )


        assertEquals(0, ((im["d"] as List<*>)[0] as Map<*, *>)["d0_"])

        val mm = PlotSpecTransform.builderForRawSpec().build().apply(im)

        assertEquals(0, mm["a"])

        DemoAndTest.assertExceptionNotHappened(Runnable { mm["a"] = 1 })

        assertEquals(2, (mm["b"] as List<*>).size)

        run {
            val list = mm["b"] as MutableList<Any>
            // maps become mutable but lists do not!
            exception.expect(UnsupportedOperationException::class.java)
            list.add(1)
            exception.expect(None::class.java)
        }

        assertEquals(0, (mm["c"] as Map<*, *>)["c_"])
        DemoAndTest.assertExceptionNotHappened(Runnable {
            val map = mm["c"] as MutableMap<Any, Any>
            map["c_"] = 1
        })

        assertEquals(0, ((mm["d"] as List<*>)[0] as Map<*, *>)["d0_"])
        DemoAndTest.assertExceptionNotHappened(Runnable {
            val map = (mm["d"] as List<*>)[0] as MutableMap<Any, Any>
            map["do_"] = 1
        })
    }
}