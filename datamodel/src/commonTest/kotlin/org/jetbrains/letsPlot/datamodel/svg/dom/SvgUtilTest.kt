/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.breadthFirstTraversal
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils.depthFirstTraversal
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgUtilTest {
    @Test
    fun dfsTest() {
        val doc = SvgSvgElement().apply {
            g(id = "A") {
                g(id = "B") {
                    g(id = "D")
                    g(id = "E")
                }
                g(id = "C") {
                    g(id = "F")
                    g(id = "G")
                }
            }
        }

        assertEquals(
            listOf("A", "B", "D", "E", "C", "F", "G"),
            depthFirstTraversal(doc).filterIsInstance<SvgGElement>().map { it.id().get() }.toList()
        )
    }

    @Test
    fun bfsTest() {
        val doc = SvgSvgElement().apply {
            g(id = "A") {
                g(id = "B") {
                    g(id = "D")
                    g(id = "E")
                }
                g(id = "C") {
                    g(id = "F")
                    g(id = "G")
                }
            }
        }

        assertEquals(
            listOf("A", "B", "C", "D", "E", "F", "G"),
            breadthFirstTraversal(doc).filterIsInstance<SvgGElement>().map { it.id().get() }.toList()
        )
    }


    private fun SvgNode.g(
        id: String? = null,
        config: SvgGElement.() -> Unit = {},
    ): SvgGElement {
        val el = SvgGElement()
        id?.let { el.id().set(it) }
        el.apply(config)
        children().add(el)
        return el
    }
}