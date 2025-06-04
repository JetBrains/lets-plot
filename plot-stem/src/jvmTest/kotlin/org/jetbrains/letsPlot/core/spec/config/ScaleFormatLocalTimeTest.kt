/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.TZ_UTC
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.checkScales
import kotlin.test.Test

/**
 * This test verifies that the annotation type `Option.Meta.SeriesAnnotation.Types.TIME` is handled correctly.
 */
class ScaleFormatLocalTimeTest {

    @Test
    fun `both - continuous and discrete scale labels - should be formatted as date-time`() {
        val instants = List(5) {
            val duration = Duration.HOUR.mul(11)
                .add(Duration.MINUTE.mul(30 * it))
            val time = Time(
                hours = duration.hour.toInt(),
                minutes = duration.minute.toInt(),
            )
            DateTime(
                Date.EPOCH,
                time
            )
        }.map {
            it.toEpochMilliseconds(TZ_UTC).toDouble()
        }

        // The same formatter is applied for both continuous and discrete scales.
        val expectedLabels = listOf(
            "11:00", "11:30", "12:00", "12:30", "13:00"
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
        val instants = List(5) {
            val duration = Duration.HOUR.mul(11)
                .add(Duration.MINUTE.mul(30 * it))
            val time = Time(
                hours = duration.hour.toInt(),
                minutes = duration.minute.toInt(),
            )
            DateTime(
                Date.EPOCH,
                time
            )
        }.map {
            it.toEpochMilliseconds(TZ_UTC).toDouble()
        }

        val formattedForContinuous = listOf(
            "11:00", "11:30", "12:00", "12:30", "13:00"
        )

        // For discrete scale: if to get the DateTimeBreaksHelper's formatter (which the continuous scale uses),
        // the labels will be formatted as follows: [00:00, 00:00, 00:00]
        // => better formatter will be applied

        // UPD: no longer an issue: the formatter is the same for both continuous and discrete scales.
        val formattedForDiscrete = listOf(
            "11:00", "11:30", "12:00", "12:30", "13:00"
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
            Option.Meta.SeriesAnnotation.TYPE to Option.Meta.SeriesAnnotation.Types.TIME
        )
    }
}