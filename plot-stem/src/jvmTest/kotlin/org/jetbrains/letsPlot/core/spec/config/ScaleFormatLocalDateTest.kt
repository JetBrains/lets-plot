/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.TZ_UTC
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.checkScales
import kotlin.test.Test

/**
 * Local date is handled the same way as date-time with no time zone specified (or UTC time zone).
 *
 * This test verifies that the annotation type `Option.Meta.SeriesAnnotation.Types.DATE` is handled correctly.
 */
class ScaleFormatLocalDateTest {

    @Test
    fun `both - continuous and discrete scale labels - should be formatted as date-time`() {
        val instants = List(5) {
            val date = Date(1, Month.JANUARY, 2021).add(Duration.DAY.mul(it))
            DateTime(date)
        }.map {
            it.toEpochMilliseconds(TZ_UTC).toDouble()
        }

        // For a discrete scale, a formatter is applied as for a continuous scale
        val expectedLabels = listOf(
            "Jan 1", "Jan 2", "Jan 3", "Jan 4", "Jan 5"
        )

        checkScales(
            instants,
            expectedLabels,
            expectedLabels,
            DATE_TIME_ANNOTATION_PART
        )
    }

    @Test
    fun `data when discrete scale chooses a better formatter than the continuous scale`() {
        val instants = List(3) {
            val date = Date(1, Month.JANUARY, 2021).add(Duration.DAY.mul(it))
            DateTime(date)
        }.map {
            it.toEpochMilliseconds(TZ_UTC).toDouble()
        }

        val formattedForContinuous = listOf(
            "00:00", "12:00", "00:00", "12:00", "00:00"
        )
        // For discrete scale: if to get the DateTimeBreaksHelper's formatter (which the continuous scale uses),
        // the labels will be formatted as follows: [00:00, 00:00, 00:00]
        // => better formatter will be applied
        val formattedForDiscrete = listOf(
            "2021-01-01", "2021-01-02", "2021-01-03"
        )

        checkScales(
            instants,
            formattedForDiscrete,
            formattedForContinuous,
            DATE_TIME_ANNOTATION_PART
        )
    }

    companion object {
        private val DATE_TIME_ANNOTATION_PART = mapOf(
            Option.Meta.SeriesAnnotation.TYPE to Option.Meta.SeriesAnnotation.Types.DATE
        )
    }
}