/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

import org.jetbrains.letsPlot.datamodel.mapping.framework.composite.Composites
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class CompositesCommonAncestorTest {

    private lateinit var a: SimpleComposite
    private lateinit var c: SimpleComposite
    private lateinit var d: SimpleComposite
    private lateinit var e: SimpleComposite
    private lateinit var g: SimpleComposite
    private lateinit var h: SimpleComposite
    private lateinit var i: SimpleComposite
    private lateinit var m: SimpleComposite
    private lateinit var r: SimpleComposite
    private lateinit var t: SimpleComposite
    private lateinit var y: SimpleComposite

    @BeforeTest
    fun init() {
        val tree = SimpleCompositesTree()
        a = tree.a
        c = tree.c
        d = tree.d
        e = tree.e
        g = tree.g
        h = tree.h
        i = tree.i
        m = tree.m
        r = tree.r
        t = tree.t
        y = tree.y
    }

    @Test
    fun differentTrees() {
        assertNull(Composites.commonAncestor(c, SimpleComposite("alien")))
    }

    @Test
    fun same() {
        assertCommonAncestor(a, a, a)
        assertCommonAncestor(d, d, d)
        assertCommonAncestor(y, y, y)
    }

    @Test
    fun ancestor() {
        assertCommonAncestor(a, h, a)
        assertCommonAncestor(c, g, c)
        assertCommonAncestor(d, m, d)
        assertCommonAncestor(d, r, d)
    }

    @Test
    fun sameLevel() {
        assertCommonAncestor(c, d, a)
        assertCommonAncestor(i, h, d)
        assertCommonAncestor(r, t, m)
    }

    @Test
    fun differentLevels() {
        assertCommonAncestor(e, c, a)
        assertCommonAncestor(m, i, d)
        assertCommonAncestor(t, i, d)
    }

    private fun assertCommonAncestor(first: SimpleComposite, second: SimpleComposite, expected: SimpleComposite) {
        assertSame(expected, Composites.commonAncestor(first, second))
    }
}
