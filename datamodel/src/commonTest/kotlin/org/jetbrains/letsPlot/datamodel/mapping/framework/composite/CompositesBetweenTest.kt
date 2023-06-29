/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

import org.jetbrains.letsPlot.datamodel.mapping.framework.composite.Composites
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompositesBetweenTest {

    private lateinit var c: SimpleComposite
    private lateinit var e: SimpleComposite
    private lateinit var f: SimpleComposite
    private lateinit var g: SimpleComposite
    private lateinit var h: SimpleComposite
    private lateinit var i: SimpleComposite
    private lateinit var k: SimpleComposite
    private lateinit var l: SimpleComposite
    private lateinit var m: SimpleComposite
    private lateinit var o: SimpleComposite
    private lateinit var p: SimpleComposite
    private lateinit var r: SimpleComposite
    private lateinit var s: SimpleComposite
    private lateinit var t: SimpleComposite
    private lateinit var u: SimpleComposite
    private lateinit var v: SimpleComposite
    private lateinit var w: SimpleComposite
    private lateinit var x: SimpleComposite
    private lateinit var y: SimpleComposite

    @BeforeTest
    fun init() {
        val tree = SimpleCompositesTree()
        c = tree.c
        e = tree.e
        f = tree.f
        g = tree.g
        h = tree.h
        i = tree.i
        k = tree.k
        l = tree.l
        m = tree.m
        o = tree.o
        p = tree.p
        r = tree.r
        s = tree.s
        t = tree.t
        u = tree.u
        v = tree.v
        w = tree.w
        x = tree.x
        y = tree.y
    }

    @Test
    fun sameRoot() {
        val root = SimpleComposite("root")
        assertBetween(root, root, listOf())
    }

    @Test
    fun sameLeaf() {
        assertBetween(g, g, listOf())
    }

    @Test
    fun nonExisting() {
        assertFailsWith<IllegalArgumentException> {
            Composites.allBetween(e, SimpleComposite("alien"))
        }
    }

    @Test
    fun reversed() {
        assertFailsWith<IllegalArgumentException> {
            Composites.allBetween(f, e)
        }
    }

    @Test
    fun twoSiblings() {
        assertConsecutiveNodes(e, f)
    }

    @Test
    fun fiveSiblings() {
        assertConsecutiveNodes(u, v, w, x, y)
    }

    @Test
    fun down() {
        assertConsecutiveNodes(k, l, r, s, t)
    }

    @Test
    fun cousins() {
        assertConsecutiveNodes(e, f, g)
    }

    @Test
    fun upSidewaysAndDown() {
        assertBetween(f, c, listOf())
        assertBetween(f, g, listOf())
        assertBetween(f, k, listOf(g, c))
        assertBetween(f, r, listOf(g, c, k, l))
        assertBetween(f, i, listOf(g, c, k, l, r, s, t, m, h))
    }

    @kotlin.test.Test
    fun upAndDown() {
        assertConsecutiveNodes(s, t, i, o, p, u, v, w, x, y)
    }

    private fun assertConsecutiveNodes(vararg nodes: SimpleComposite) {
        val nodeList = listOf(*nodes)
        for (i in nodeList.indices) {
            val left = nodeList[i]
            for (j in i + 1 until nodeList.size) {
                val right = nodeList[j]
                val expectedSubList = nodeList.subList(i + 1, j)
                assertBetween(left, right, expectedSubList)
            }
        }
    }

    private fun assertBetween(left: SimpleComposite?, right: SimpleComposite?, expected: List<SimpleComposite>) {
        assertEquals(expected, Composites.allBetween(left!!, right!!), "left: $left, right: $right")
    }
}
