package jetbrains.datalore.base.assertion

import kotlin.math.abs
import kotlin.test.assertTrue

fun assertEquals(expected: Double?, actual: Double?, precision: Double, message: String? = null): Unit {
    var equal: Boolean = actual == expected
    if (!equal && expected != null) {
        equal = abs(expected - actual!!) <= precision
    }

    assertTrue(equal, messagePrefix(message) + "Expected <$expected>, actual <$actual>.")
}

fun assertArrayEquals(expecteds: Array<Any>, actuals: Array<Any>, message: String? = null) {
    assertTrue(actuals contentEquals expecteds, message)
}

private fun messagePrefix(message: String?): String {
    return if (message == null) "" else "$message "
}