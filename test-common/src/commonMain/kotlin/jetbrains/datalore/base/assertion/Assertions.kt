/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.assertion

import jetbrains.datalore.base.gcommon.base.Throwables
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

fun assertEquals(expected: Double?, actual: Double?, precision: Double, message: String? = null) {
    assertTrue(
        doubleEquals(expected, actual, precision),
        messagePrefix(message) + "Expected <$expected>, actual <$actual>."
    )
}

fun <T> assertArrayEquals(expecteds: Array<T>, actuals: Array<T>, message: String? = null) {
    assertTrue(actuals contentEquals expecteds, message)
}

fun assertArrayEquals(expecteds: Array<Double>, actuals: Array<Double>, precision: Double, message: String? = null) {
    assertEquals(expecteds.size, actuals.size, message)
    assertTrue((0 until expecteds.size).all { i -> doubleEquals(expecteds[i], actuals[i], precision) }, message)
}

private fun doubleEquals(lft: Double?, rgt: Double?, precision: Double): Boolean {
    var equal: Boolean = lft == rgt
    if (!equal && lft != null) {
        equal = rgt != null && abs(lft - rgt) <= precision
    }
    return equal
}

private fun messagePrefix(message: String?): String {
    return if (message == null) "" else "$message "
}

fun assertDoesNotFail(r: () -> Unit) {
    assertDoesNotFail("", r)
}

fun assertDoesNotFail(message: String, r: () -> Unit) {
    try {
        r()
    } catch (e: RuntimeException) {
        val rootCause = Throwables.getRootCause(e)
        fail(message + rootCause.message, cause = rootCause)
    }
}


