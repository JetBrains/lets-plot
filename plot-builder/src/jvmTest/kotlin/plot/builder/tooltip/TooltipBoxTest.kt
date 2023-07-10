/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.HORIZONTAL
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation.VERTICAL
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TooltipBoxTest {
    private lateinit var peer: MockSvgPlatformPeer
    private lateinit var tooltipBox: TooltipBox
    private val root = SvgSvgElement()
    @Suppress("unused") // removing of this val will break all tests
    private val container = SvgNodeContainer(root)
    private val wordSize = DoubleVector(40.0, 40.0)
    private val wordText = "WORD"
    private val word = wordText to wordSize

    @BeforeTest
    fun before() {
        peer = MockSvgPlatformPeer().apply {
            root.container().setPeer(this)
            labelsBbox(word)
        }

        tooltipBox = TooltipBox().apply {
            root.children().add(rootGroup)
            update(
                fillColor = Color.BLACK,
                textColor = Color.WHITE,
                borderColor = Color.BLACK,
                strokeWidth = 1.0,
                lines = listOf(TooltipSpec.Line.withValue(wordText)),
                title = null,
                textClassName = "anyStyle",
                rotate = false,
                borderRadius = 0.0,
                markerColors = emptyList()
            )
        }
    }

    @Test
    fun nullDirectionCases() {
        tooltipBox.apply {
            setPosition(ZERO, wordSize.mul(0.5), HORIZONTAL)
            assertNull(pointerDirection, "Pointer inside tooltip - direction should be null")
        }

        tooltipBox.apply {
            setPosition(ZERO, p(wordSize.x / 2.0, wordSize.y + 100.0), HORIZONTAL)
            assertNull(pointerDirection, "Pointer x coord within tooltips x range - direction should be null")
        }

        tooltipBox.apply {
            setPosition(ZERO, p(wordSize.x + 100.0, 4.0), VERTICAL)
            assertNull(pointerDirection, "Pointer y coord within tooltips y range - direction should be null")
        }
    }

    @Test
    fun verticalDirectionCases() {
        tooltipBox.apply {
            setPosition(ZERO, wordSize.add(p(0.0, 20.0)), VERTICAL)
            assertEquals(
                pointerDirection,
                TooltipBox.PointerDirection.DOWN,
                "Pointer above tooltip - PointerDirection.DOWN"
            )
        }

        tooltipBox.apply {
            setPosition(ZERO, p(0.0, -10.0), VERTICAL)
            assertEquals(
                pointerDirection,
                TooltipBox.PointerDirection.UP,
                "Pointer under tooltip - PointerDirection.UP"
            )
        }
    }

    @Test
    fun horizontalDirectionCases() {
        tooltipBox.apply {
            setPosition(ZERO, wordSize.add(p(20.0, 0.0)), HORIZONTAL)
            assertEquals(
                pointerDirection,
                TooltipBox.PointerDirection.RIGHT,
                "Pointer right from tooltip - PointerDirection.LEFT"
            )
        }

        tooltipBox.apply {
            setPosition(ZERO, p(-10.0, 0.0), HORIZONTAL)
            assertEquals(
                pointerDirection,
                TooltipBox.PointerDirection.LEFT,
                "Pointer left from tooltip - PointerDirection.RIGHT"
            )
        }
    }


    private fun p(x: Double, y: Double) = DoubleVector(x, y)

    class MockSvgPlatformPeer : SvgPlatformPeer {
        private val myLabelBboxes = mutableMapOf<String, DoubleVector>()
        override fun getComputedTextLength(node: SvgTextContent): Double {
            UNSUPPORTED()
        }

        override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
            UNSUPPORTED()
        }

        override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
            UNSUPPORTED()
        }

        override fun getBBox(element: SvgLocatable): DoubleRectangle {
            // TextLabel/MultilineLabel
            try {
                return element
                    .run { (this as SvgGElement).children()[0] }
                    .run {
                        val textElem = (this as SvgTextElement).children()[0]
                        if (textElem is SvgTSpanElement) {
                            textElem.children()[0]
                        } else {
                            textElem
                        }
                    }
                    .run { (this as SvgTextNode).textContent().get() }
                    .run { myLabelBboxes[this]!! }
                    .run { DoubleRectangle(0.0, -this.y, this.x, 0.0) }
            } catch (e: Throwable) {
            }

            // another type
            try {
            } catch (e: Throwable) {
            }

            if (element is SvgElement) {
                error("Unknown element: ${SvgNodeBufferUtil.generateSvgNodeBuffer(element)}")
            } else {
                error("")
            }
        }

        fun labelsBbox(vararg sizes: Pair<String, DoubleVector>) {
            myLabelBboxes.putAll(sizes)
        }
    }
}
