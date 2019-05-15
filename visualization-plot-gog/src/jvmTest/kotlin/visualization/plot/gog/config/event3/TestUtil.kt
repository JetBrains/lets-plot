package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.event3.MappedDataAccessMock.Companion.variable
import jetbrains.datalore.visualization.plot.gog.plot.event3.MappedDataAccessMock.Mapping
import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.TooltipSpec

import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestUtil {
    private const val VARIABLE_NAME = "A"
    private const val VARIABLE_VALUE = "value"

    internal fun <T> continuous(aes: Aes<T>): Mapping<T> {
        return mappedData(aes, true)
    }

    internal fun <T> discrete(aes: Aes<T>): Mapping<T> {
        return mappedData(aes, false)
    }

    private fun <T> mappedData(aes: Aes<T>, isContinuous: Boolean): Mapping<T> {
        return variable().name(VARIABLE_NAME).value(VARIABLE_VALUE).isContinuous(isContinuous).mapping(aes)
    }

    internal fun assertText(tooltipSpecs: List<TooltipSpec>, vararg expectedTooltipText: String) {
        assertText(tooltipSpecs, listOf(*expectedTooltipText))
    }

    @SafeVarargs
    internal fun assertText(tooltipSpecs: List<TooltipSpec>, vararg expectedTooltips: List<String>) {
        assertEquals(expectedTooltips.size.toLong(), tooltipSpecs.size.toLong())
        var i = 0
        val n = tooltipSpecs.size
        while (i < n) {
            val tooltipText = tooltipSpecs[i].lines
            assertListsEqual(expectedTooltips[i], tooltipText)
            ++i
        }
    }

    private fun <T> assertListsEqual(expected: List<T>, actual: List<T>) {
        assertEquals(expected.size.toLong(), actual.size.toLong())
        var i = 0
        val n = expected.size
        while (i < n) {
            assertEquals(expected[i], actual[i])
            ++i
        }
    }

    internal fun assertNoTooltips(tooltipSpecs: List<TooltipSpec>) {
        assertTrue(tooltipSpecs.isEmpty())
    }
}
