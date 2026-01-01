/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import kotlin.test.Test

class SvgComplianceTest {
    @Test
    fun `nested g - empty`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(translate(x = 10, y = 20), id = "g")
            }
        }

        doc.findElement<Group>("g").let {
            assertThat(it.transform).isEqualTo(AffineTransform.makeTranslation(10f, 20f))
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(0f, 0f, 0f, 0f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(10f, 20f, 0f, 0f))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslation(10f, 20f))
        }
    }


    @Test
    fun `nested g - simple`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(translate(x = 10, y = 20), id = "g") {
                    rect(5, 16, 40, 15, strokeWidth = 0, fill = SvgColors.RED, id = "rect")
                }
            }
        }

        doc.findElement<Rectangle>("rect").let {
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(15, 36, 40, 15))
        }

        doc.findElement<Group>("g").let {
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(5f, 16f, 40f, 15f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(15, 36, 40, 15))

            assertThat(it.transform).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
        }
    }

    @Test
    fun `nested svg - empty`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                svg(x = 10, y = 20, width = 30, height = 40, id = "svg")
            }
        }

        doc.findElement<Pane>("svg").let {
            assertThat(it.transform).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(0f, 0f, 0f, 0f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(10f, 20f, 0f, 0f))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
        }
    }

    @Test
    fun `nested svg - simple`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                svg(x = 10, y = 20, width = 400, height = 300, id = "svg") {
                    rect(5, 16, 40, 15, strokeWidth = 0, fill = SvgColors.RED, id = "rect")
                }
            }
        }

        doc.findElement<Rectangle>("rect").let {
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(15, 36, 40, 15))
        }

        doc.findElement<Pane>("svg").let {
            assertThat(it.transform).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(5f, 16f, 40f, 15f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(15, 36, 40, 15))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(10f, 20f))
        }
    }

    @Test
    fun `group - empty children should not affect measurement`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "root_g") {
                    g(translate(50, 70), id = "sub_g") {
                        rect(0, 0, 10, 20, strokeWidth = 0)
                        g(translate(500, 500), id = "empty_g") // no children - should be excluded from measurement
                    }
                }
            }
        }

        doc.findElement<Group>("root_g").let {
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(50, 70, 10, 20))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(50, 70, 10, 20))
        }

        doc.findElement<Group>("sub_g").let {
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(0, 0, 10, 20))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(50, 70, 10, 20))
        }

        doc.findElement<Group>("empty_g").let {
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(0, 0, 0, 0))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(550, 570, 0, 0))
        }
    }

    @Test
    fun `rect xy doesnt affect screenTransform`() {
        val doc = mapSvg {
            svgDocument(width = 400.0, height = 300.0) {
                g(translate(10.0, 20.0)) {
                    g(translate(30.0, 50.0)) {
                        rect(x = 3f, y = 5f, width = 10f, height = 10f, strokeWidth = 0, id = "rect")
                    }
                }
            }
        }

        doc.findElement<Rectangle>("rect").let {
            assertThat(it.transform).isEqualTo(AffineTransform.IDENTITY)
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(3, 5, 10, 10))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(43, 75, 10, 10))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(40, 70))
        }
    }

    @Test
    fun nestedSvgSvgElement() {
        val doc = mapSvg {
            svgDocument(width = 400.0, height = 300.0) {
                g(translate(10.0, 20.0)) {
                    svg(x = 13.0, y = 17.0, width = 180.0, height = 50.0, "svg") {
                        rect(x = 1f, y = 3f, width = 10f, height = 10f, strokeWidth = 0, id = "rect")
                    }
                }
            }
        }

        doc.findElement<Pane>("svg").let {
            assertThat(it.transform).isEqualTo(AffineTransform.makeTranslate(13f, 17f))
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(1f, 3f, 10f, 10f))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(23f, 37f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(24, 40, 10, 10))
        }


        doc.findElement<Rectangle>("rect").let {
            assertThat(it.transform).isEqualTo(AffineTransform.IDENTITY)
            assertThat(it.bBoxLocal).isEqualTo(DoubleRectangle.XYWH(1f, 3f, 10f, 10f))
            assertThat(it.bBoxGlobal).isEqualTo(DoubleRectangle.XYWH(24, 40, 10, 10))
            assertThat(it.ctm).isEqualTo(AffineTransform.makeTranslate(23f, 37f))
        }
    }

    @Test
    fun `depth first traversal`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "0") {
                    g(id = "1") {
                        rect(id = "2")
                        rect(id = "3")
                        rect(id = "4")
                    }
                    g(id = "5") {
                        g(id = "6") {
                            rect(id = "7")
                            rect(id = "8")
                        }
                    }
                }
                rect(id = "9")
            }
        }

        val elements = depthFirstTraversal(doc,).toList()

        assertThat(elements.map(Node::id)).isEqualTo(listOf(null, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
    }

    @Test
    fun `reversed depth first traversal`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "0") {
                    g(id = "1") {
                        rect(id = "2")
                        rect(id = "3")
                        rect(id = "4")
                    }
                    g(id = "5") {
                        g(id = "6") {
                            rect(id = "7")
                            rect(id = "8")
                        }
                    }
                }
                rect(id = "9")
            }
        }

        val elements = reversedDepthFirstTraversal(doc).toList()

        assertThat(elements.map(Node::id)).isEqualTo(listOf("9", "8", "7", "6", "5", "4", "3", "2", "1", "0", null))
    }

    @Test
    fun `breadth first traversal`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "0") {
                    g(id = "1") {
                        rect(id = "2")
                        rect(id = "3")
                        rect(id = "4")
                    }
                    g(id = "5") {
                        g(id = "6") {
                            rect(id = "7")
                            rect(id = "8")
                        }
                    }
                }
                rect(id = "9")
            }
        }

        val elements = breadthFirstTraversal(doc).toList()

        assertThat(elements.map(Node::id)).isEqualTo(listOf(null, "0", "9", "1", "5", "2", "3", "4", "6", "7", "8"))
    }

    @Test
    fun `reversed breadth first traversal`() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "0") {
                    g(id = "1") {
                        rect(id = "2")
                        rect(id = "3")
                        rect(id = "4")
                    }
                    g(id = "5") {
                        g(id = "6") {
                            rect(id = "7")
                            rect(id = "8")
                        }
                    }
                }
                rect(id = "9")
            }
        }

        val elements = reversedBreadthFirstTraversal(doc).toList()

        assertThat(elements.map(Node::id)).isEqualTo(listOf("8", "7", "6", "4", "3", "2", "5", "1", "9", "0", null))
    }

}
