/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.interact.TestUtil.size
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.*
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment.LEFT
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal open class TooltipLayoutTestBase {

    private lateinit var myArrangedTooltips: MutableList<TooltipHelper>
    private lateinit var myTooltipDataProvider: TooltipDataProvider

    fun createTipLayoutManagerBuilder(viewport: DoubleRectangle): TipLayoutManagerBuilder {
        val tipLayoutManagerBuilder = TipLayoutManagerBuilder(viewport)
        this.myTooltipDataProvider = tipLayoutManagerBuilder
        return tipLayoutManagerBuilder
    }

    fun arrange(layoutManagerController: TipLayoutManagerController) {
        this.myArrangedTooltips = ArrayList()

        for (tooltipEntry in layoutManagerController.arrange()) {
            val measuredTooltip = myTooltipDataProvider[tooltipEntry.tooltipSpec.lines]!!
            myArrangedTooltips.add(TooltipHelper(tooltipEntry, measuredTooltip))
        }
    }

    fun tooltip(tooltipKey: String): TooltipHelper {
        val strings = listOf(tooltipKey)

        for (tooltip in myArrangedTooltips) {
            if (tooltip.text == strings) {
                return tooltip
            }
        }

        error("Tooltip $tooltipKey is not found")
    }

    fun expectedSideTipY(tooltipKey: String): Double {
        return tooltip(tooltipKey).run { stemCoord().y - rect().height / 2 }
    }

    fun expectedSideTipX(key: String, alignment: HorizontalAlignment): Double {
        return expectedHorizontalX(key, alignment, NORMAL_STEM_LENGTH)
    }

    private fun expectedHorizontalX(tooltipKey: String, alignment: HorizontalAlignment, stemLength: Double): Double {
        val tooltip = tooltip(tooltipKey)

        return when {
            alignment === HorizontalAlignment.RIGHT ->
                tooltip.cfgHintCoord().x + tooltip.cfgHintRadius() + stemLength
            alignment === LEFT ->
                tooltip.cfgHintCoord().x - tooltip.cfgHintRadius() - stemLength - tooltip.size().x
            else ->
                throw IllegalArgumentException("Center alignment is not supported for this tooltip's kind")
        }
    }

    fun expectedAroundPointX(tooltipKey: String): Double {
        val tooltip = tooltip(tooltipKey)

        return tooltip.cfgHintCoord().x - tooltip.size().x / 2
    }

    fun expectedAroundPointY(tooltipKey: String, verticalAlignment: VerticalAlignment): Double {
        val tooltip = tooltip(tooltipKey)

        return when (verticalAlignment) {
            VerticalAlignment.TOP ->
                tooltip.cfgHintCoord().y - tooltip.size().y - NORMAL_STEM_LENGTH - tooltip.cfgHintRadius()
            VerticalAlignment.BOTTOM ->
                tooltip.cfgHintCoord().y + NORMAL_STEM_LENGTH + tooltip.cfgHintRadius()

            else -> throw IllegalArgumentException("Placement is not supported: $verticalAlignment")
        }
    }

    fun expectedAroundPointStem(tooltipKey: String): DoubleVector {
        val tooltip = tooltip(tooltipKey)

        val hintCoord = tooltip.cfgHintCoord()
        val hintRadius = tooltip.cfgHintRadius()

        return hintCoord.add(size(0.0, hintRadius))
    }

    fun expectedAxisTipY(tooltipKey: String, verticalAlignment: VerticalAlignment): Double {
        val tooltip = tooltip(tooltipKey)

        return when (verticalAlignment) {

            VerticalAlignment.TOP ->
                tooltip.cfgHintCoord().y - tooltip.size().y - AXIS_STEM_LENGTH
            VerticalAlignment.BOTTOM ->
                tooltip.cfgHintCoord().y + AXIS_STEM_LENGTH

            else -> throw IllegalArgumentException("Placement is not supported: $verticalAlignment")
        }
    }

    fun expectedAxisTipX(tooltipKey: String, alignment: HorizontalAlignment): Double {
        return expectedHorizontalX(tooltipKey, alignment, AXIS_STEM_LENGTH)
    }

    private fun <T> shouldCheck(v: T?): Boolean {
        return v != null
    }

    fun assertAllTooltips(vararg expectations: ExpectedTooltip) {
        assertEquals(expectations.size, myArrangedTooltips.size)

        var i = 0
        val n = expectations.size
        while (i < n) {
            assertExpectations(expectations[i], myArrangedTooltips[i])
            ++i
        }
    }

    private fun assertExpectations(expectedTooltip: ExpectedTooltip, actual: TooltipHelper) {
        if (shouldCheck(expectedTooltip.text())) {
            assertEquals(makeText(expectedTooltip.text()!!), actual.text)
        }

        if (shouldCheck(expectedTooltip.tooltipX())) {
            assertDoubleEquals("tooltipX", expectedTooltip.tooltipX()!!, actual.coord().x)
        }

        if (shouldCheck(expectedTooltip.tooltipY())) {
            assertDoubleEquals("tooltipY", expectedTooltip.tooltipY()!!, actual.coord().y)
        }

        if (shouldCheck(expectedTooltip.stemX())) {
            assertDoubleEquals("stemX", expectedTooltip.stemX()!!, actual.stemCoord().x)
        }

        if (shouldCheck(expectedTooltip.stemY())) {
            assertDoubleEquals("stemY", expectedTooltip.stemY()!!, actual.stemCoord().y)
        }
    }

    private fun assertDoubleEquals(message: String, expected: Double, actual: Double) {
        assertEquals(expected, actual, DOUBLE_COMPARE_EPSILON, message)
    }

    fun assertInsideView(viewport: DoubleRectangle) {
        for (arrangedTooltip in myArrangedTooltips) {
            val tooltip = arrangedTooltip.rect()
            for (side in tooltip.parts) {
                assertTrue(viewport.contains(side.start))
                assertTrue(viewport.contains(side.end))
            }
        }
    }

    fun expect(): ExpectedTooltip {
        return ExpectedTooltip()
    }

    fun expect(tooltipKey: String): ExpectedTooltip {
        return ExpectedTooltip().text(tooltipKey)

    }

    fun orderedListOf(count: Int): Array<ExpectedTooltip> {
        val expectedTooltips = ArrayList<ExpectedTooltip>()
        for (i in 0 until count) {
            expectedTooltips.add(expect().text((i + 1).toString()))
        }

        return expectedTooltips.toTypedArray()
    }

    internal interface TipLayoutManagerController {
        fun arrange(): List<PositionedTooltip>
    }

    internal interface TooltipDataProvider {
        operator fun get(lines: List<TooltipSpec.Line>): MeasuredTooltip?
    }

    internal class TipLayoutManagerBuilder(private val myViewport: DoubleRectangle) : TooltipDataProvider {
        private val myTooltipData = ArrayList<MeasuredTooltip>()
        private val myHorizontalAlignment: HorizontalAlignment = LEFT
        private var myCursor = DoubleVector.ZERO

        fun cursor(cursor: DoubleVector): TipLayoutManagerBuilder {
            myCursor = cursor
            return this
        }

        fun addTooltip(measuredTooltip: MeasuredTooltip): TipLayoutManagerBuilder {
            myTooltipData.add(measuredTooltip)
            return this
        }

        fun build(): TipLayoutManagerController {
            return object : TipLayoutManagerController {
                override fun arrange(): List<PositionedTooltip> =
                    LayoutManager(myViewport, myHorizontalAlignment)
                        .arrange(myTooltipData, myCursor, geomBounds = null)
            }
        }

        override fun get(lines: List<TooltipSpec.Line>): MeasuredTooltip? {
            for (measuredTooltip in myTooltipData) {
                if (measuredTooltip.tooltipSpec.lines == lines) {
                    return measuredTooltip
                }
            }
            return null
        }
    }

    internal class TooltipHelper(private val myTooltipEntry: PositionedTooltip, private val myMeasuredTooltip: MeasuredTooltip) {
        private val myHintRadius: Double = myMeasuredTooltip.hintRadius
        private val myTooltipRect: DoubleRectangle = DoubleRectangle(myTooltipEntry.tooltipCoord, myMeasuredTooltip.size)

        val text get() = myTooltipEntry.tooltipSpec.lines.map(TooltipSpec.Line::toString)
        val fill get() = myTooltipEntry.tooltipSpec.fill
        fun coord() = myTooltipEntry.tooltipCoord
        fun stemCoord() = myTooltipEntry.stemCoord
        fun rect() = myTooltipRect
        fun size() = myMeasuredTooltip.size
        fun cfgHintRadius() = myHintRadius
        fun cfgHintCoord() = myMeasuredTooltip.hintCoord
    }

    internal class ExpectedTooltip {
        private var text: String? = null
        private var tooltipX: Double? = null
        private var tooltipY: Double? = null
        private var stemX: Double? = null
        private var stemY: Double? = null

        fun text(text: String): ExpectedTooltip {
            this.text = text
            return this
        }

        fun tooltipX(tooltipX: Double?): ExpectedTooltip {
            this.tooltipX = tooltipX
            return this
        }

        fun tooltipY(tooltipY: Double?): ExpectedTooltip {
            this.tooltipY = tooltipY
            return this
        }

        fun tooltipCoord(tooltipCoord: DoubleVector): ExpectedTooltip {
            this.tooltipX = tooltipCoord.x
            this.tooltipY = tooltipCoord.y
            return this
        }

        fun stemCoord(stemCoord: DoubleVector): ExpectedTooltip {
            this.stemX = stemCoord.x
            this.stemY = stemCoord.y
            return this
        }

        fun text(): String? {
            return text
        }

        fun tooltipX(): Double? {
            return tooltipX
        }

        fun tooltipY(): Double? {
            return tooltipY
        }

        fun stemX(): Double? {
            return stemX
        }

        fun stemY(): Double? {
            return stemY
        }
    }

    companion object {
        val VIEWPORT = DoubleRectangle(0.0, 0.0, 500.0, 500.0)
        val DEFAULT_TOOLTIP_SIZE = DoubleVector(80.0, 40.0)

        const val DEFAULT_OBJECT_RADIUS = 40.0
        private const val DOUBLE_COMPARE_EPSILON = 0.01

        val NORMAL_STEM_LENGTH = TipLayoutHint.StemLength.NORMAL.value
        val AXIS_STEM_LENGTH = TipLayoutHint.StemLength.NONE.value

        fun makeText(text: String): List<String> {
            return listOf(text)
        }
    }
}
