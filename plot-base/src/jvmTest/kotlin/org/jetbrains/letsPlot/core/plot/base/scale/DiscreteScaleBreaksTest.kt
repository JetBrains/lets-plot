/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class DiscreteScaleBreaksTest(
    private val domainValues: List<String>,
    private val breaks: List<String>?,
    private val limits: List<String>?,
    private val labels: List<String>?,
    private val expectedBreaks: List<String>,
    private val expectedLabels: List<String>,
) {

    @Test
    fun checkBreaksAndLabels() {
        var scale = Scales.DemoAndTest.discreteDomain(
            "Discr. scale",
            domainValues,
            domainLimits = limits ?: emptyList()
        )
        breaks?.run {
            scale = scale.with().breaks(this).build()
        }
        labels?.run {
            scale = scale.with().labels(this).build()
        }

        // ---
        limits?.run {
            assertTrue(scale.transform.hasDomainLimits())
        }
        assertTrue(scale.hasBreaks())

        assertEquals(expectedBreaks, scale.getScaleBreaks().domainValues)
        assertEquals(expectedLabels, scale.getScaleBreaks().labels)
    }


    companion object {
        private val DOMAIN = listOf("a", "b", "c")

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                args(
                    DOMAIN,
                ),
                //
                // breaks
                //
                args(
                    DOMAIN,
                    breaks = listOf("b"), // inside
                    expectedBreaks = listOf("b"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c", "d"), // intersect
                    expectedBreaks = listOf("b", "c"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("d", "e"), // outside
                    expectedBreaks = emptyList(),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("c", "b", "a"), // order (ignored)
                ),
                //
                // limits
                //
                args(
                    DOMAIN,
                    limits = listOf("b"), // inside
                    expectedBreaks = listOf("b"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("b", "c", "d"), // intersect
                    expectedBreaks = listOf("b", "c", "d"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("d", "e"), // outside (error in R)
                    expectedBreaks = listOf("d", "e"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("c", "b", "a"), // order (applied)
                    expectedBreaks = listOf("c", "b", "a"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("d", "c", "b"), // order (applied)
                    expectedBreaks = listOf("d", "c", "b"),
                ),
                // labels
                args(
                    DOMAIN,
                    labels = listOf("a-lab", "b-lab", "c-lab"),
                    expectedLabels = listOf("a-lab", "b-lab", "c-lab"),
                ),
                args(
                    DOMAIN,
                    labels = listOf("b-lab"),
                    expectedLabels = listOf("b-lab", "", ""),
                ),
                //
                // breaks + limits
                //
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("b"),
                    expectedBreaks = listOf("b"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("c", "d"),
                    expectedBreaks = listOf("c"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("c", "b"), // order
                    expectedBreaks = listOf("c", "b"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("d"),  // error in R
                    expectedBreaks = emptyList(),
                ),

                //
                // breaks + labels
                //
                args(
                    DOMAIN,
                    breaks = listOf("b"), // inside
                    labels = listOf("b-lab"),
                    expectedBreaks = listOf("b"),
                    expectedLabels = listOf("b-lab"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    labels = listOf("b-lab", "c-lab", "d-lab"), // error in R
                    expectedBreaks = listOf("b", "c"),
                    expectedLabels = listOf("b-lab", "c-lab"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    labels = listOf("b-lab"), // error in R
                    expectedBreaks = listOf("b", "c"),
                    expectedLabels = listOf("b-lab", ""),
                ),

                //
                // limits + labels
                //
                args(
                    DOMAIN,
                    limits = listOf("b", "c"),
                    labels = listOf("a-lab", "b-lab", "c-lab"),
                    expectedBreaks = listOf("b", "c"),
                    expectedLabels = listOf("a-lab", "b-lab"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("b", "c", "d"),
                    labels = listOf("a-lab", "b-lab", "c-lab"),
                    expectedBreaks = listOf("b", "c", "d"),
                    expectedLabels = listOf("a-lab", "b-lab", "c-lab"),
                ),
                args(
                    DOMAIN,
                    limits = listOf("d", "c", "b"),  // reversed
                    labels = listOf("a-lab", "b-lab", "c-lab"),
                    expectedBreaks = listOf("d", "c", "b"),
                    expectedLabels = listOf("a-lab", "b-lab", "c-lab"),
                ),

                //
                // breaks + limits + labels
                //
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("c"),
                    labels = listOf("b-lab", "c-lab"),
                    expectedBreaks = listOf("c"),
                    expectedLabels = listOf("c-lab"),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("d"),  // error in R
                    labels = listOf("b-lab", "c-lab"),
                    expectedBreaks = emptyList(),
                    expectedLabels = emptyList(),
                ),
                args(
                    DOMAIN,
                    breaks = listOf("b", "c"),
                    limits = listOf("c", "b"),   // reversed
                    labels = listOf("b-lab", "c-lab"),
                    expectedBreaks = listOf("c", "b"),
                    expectedLabels = listOf("c-lab", "b-lab"),
                ),
            )
        }

        private fun args(
            domainValues: List<String>,
            breaks: List<String>? = null,
            limits: List<String>? = null,
            labels: List<String>? = null,
            expectedBreaks: List<String> = domainValues,
            expectedLabels: List<String> = expectedBreaks,
        ): Array<Any?> {
            return arrayOf(
                domainValues,
                breaks,
                limits,
                labels,
                expectedBreaks,
                expectedLabels
            )
        }
    }
}