/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.TZ_UTC
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.checkScales
import kotlin.test.Test

/**
 * TODO: Handle local time correctly
 * Currently local time is handled the same way as date-time with no time zone specified (or UTC time zone).
 *
 * This test verifies that the annotation type `Option.Meta.SeriesAnnotation.Types.TIME` is handled correctly.
 */
class ScaleFormatLocalTimeTest {

    @Test
    fun `both - continuous and discrete scale labels - should be formatted as date-time`() {
        val instants = List(5) {
            val duration = Duration.HOUR.mul(11)
                .add(Duration.MINUTE.mul(30 * it.toLong()))
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

        // For a discrete scale, a formatter is applied as for a continuous scale
        // TODO: actually we expect: ["11:00", "11:30", "12:00", "12:30", "13:00"]
        val expectedLabels = listOf(
            "00", "30", "00", "30", "00"
        )
        val expectedLabels2 = listOf(
            "1970-01-01 11:00", "1970-01-01 11:30", "1970-01-01 12:00", "1970-01-01 12:30", "1970-01-01 13:00"
        )

        checkScales(
            instants,
            expectedLabels2,
            expectedLabels,
            DATE_TIME_ANNOTATION_PART
        )
    }

    @Test
    fun `data when discrete scale chooses a better formatter than the continuous scale`() {
        val instants = List(5) {
            val duration = Duration.HOUR.mul(11)
                .add(Duration.MINUTE.mul(30 * it.toLong()))
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

        // TODO: actually we expect: ["11:00", "11:30", "12:00", "12:30", "13:00"]
        val formattedForContinuous = listOf(
            "00", "30", "00", "30", "00"
        )

        // For discrete scale: if to get the DateTimeBreaksHelper's formatter (which the continuous scale uses),
        // the labels will be formatted as follows: [00:00, 00:00, 00:00]
        // => better formatter will be applied
        // TODO: actually we expect: ["11:00", "11:30", "12:00", "12:30", "13:00"]
        val formattedForDiscrete = listOf(
            "1970-01-01 11:00", "1970-01-01 11:30", "1970-01-01 12:00", "1970-01-01 12:30", "1970-01-01 13:00"
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