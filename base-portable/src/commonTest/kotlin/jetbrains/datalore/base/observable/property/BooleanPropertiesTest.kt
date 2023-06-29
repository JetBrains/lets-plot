/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BooleanPropertiesTest {
    companion object {
        private val TRUE = ValueProperty<Boolean?>(true)
        private val FALSE = ValueProperty<Boolean?>(false)
        private val NULL = ValueProperty<Boolean?>(null)
    }

    @Test
    fun not() {
        assertFalse(Properties.not(TRUE).get()!!)
        assertTrue(Properties.not(FALSE).get()!!)
        assertNull(Properties.not(NULL).get())
    }

    @Test
    fun and() {
        assertTrue(Properties.and(TRUE, TRUE).get() as Boolean)
        assertFalse(Properties.and(TRUE, FALSE).get() as Boolean)
        assertNull(Properties.and(TRUE, NULL).get())
        assertFalse(Properties.and(FALSE, TRUE).get() as Boolean)
        assertFalse(Properties.and(FALSE, FALSE).get() as Boolean)
        assertFalse(Properties.and(FALSE, NULL).get() as Boolean)
        assertNull(Properties.and(NULL, TRUE).get())
        assertFalse(Properties.and(NULL, FALSE).get() as Boolean)
        assertNull(Properties.and(NULL, NULL).get())
    }

    @Test
    fun or() {
        assertTrue(Properties.or(TRUE, TRUE).get() as Boolean)
        assertTrue(Properties.or(TRUE, FALSE).get() as Boolean)
        assertTrue(Properties.or(TRUE, NULL).get() as Boolean)
        assertTrue(Properties.or(FALSE, TRUE).get() as Boolean)
        assertFalse(Properties.or(FALSE, FALSE).get() as Boolean)
        assertNull(Properties.or(FALSE, NULL).get())
        assertTrue(Properties.or(NULL, TRUE).get() as Boolean)
        assertNull(Properties.or(NULL, FALSE).get())
        assertNull(Properties.or(NULL, NULL).get())
    }

    @Test
    fun multipleAnd() {
        assertTrue(Properties.and(TRUE, TRUE, TRUE).get() as Boolean)
        assertFalse(Properties.and(TRUE, FALSE, TRUE).get() as Boolean)
        assertFalse(Properties.and(TRUE, NULL, FALSE).get() as Boolean)
        assertNull(Properties.and(TRUE, NULL, TRUE).get())
    }

    @Test
    fun multipleOr() {
        assertFalse(Properties.or(FALSE, FALSE, FALSE).get() as Boolean)
        assertTrue(Properties.or(FALSE, FALSE, TRUE).get() as Boolean)
        assertTrue(Properties.or(FALSE, NULL, TRUE).get() as Boolean)
        assertNull(Properties.or(FALSE, NULL, FALSE).get())
    }
}