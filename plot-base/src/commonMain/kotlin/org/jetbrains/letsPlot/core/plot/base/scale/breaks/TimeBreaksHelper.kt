/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.WEEK
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import kotlin.math.abs
import kotlin.math.ceil

internal class TimeBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int,
    private val providedFormatter: ((Any) -> String)?,
    tz: TimeZone?,
) : BreaksHelperBase(rangeStart, rangeEnd, count) {

    override val breaks: List<Double>
    val formatter: (Any) -> String

    private val dayFormat = newStringFormat("{d}d", tz)
    private val hmsFormat = newStringFormat("{d}:{02d}:{02d}", tz)
    private val hmFormat = newStringFormat("{d}:{02d}", tz)

    init {
        val ticks: List<Double> = when {
            targetStep < 1000 -> LinearBreaksHelper(
                rangeStart, rangeEnd, count,
                providedFormatter = DUMMY_FORMATTER,
                expFormat = DEF_EXPONENT_FORMAT,
            ).breaks

            else -> computeNiceTicks()
        }

        breaks = when (isReversed) {
            true -> ticks.reversed()
            false -> ticks
        }

        formatter = providedFormatter ?: { v -> formatString((v as Number).toLong()) }
    }

    private fun formatTotalDays(duration: Duration) = dayFormat.apply(duration.totalDays)
    private fun formatHms(duration: Duration) = hmsFormat.apply(duration.hour, duration.minute, duration.second)
    private fun formatHm(duration: Duration) = hmFormat.apply(duration.hour, duration.minute)

    fun formatBreaks(ticks: List<Double>): List<String> {
        return when {
            targetStep < 1000 -> ticks.map(formatter)
            else -> {
                val hideSeconds = ticks.all { it >= DAY.totalMillis || Duration(it.toLong()).second == 0L }
                ticks.map { formatString(it.toLong(), hideSeconds = hideSeconds) }
            }
        }
    }

    private fun formatString(v: Long, hideSeconds: Boolean = false): String {
        if (v == 0L) {
            return "0"
        }

        val duration = Duration(abs(v))

        val parts = mutableListOf<String>()
        if (duration.totalDays > 0) {
            parts.add(formatTotalDays(duration))
        }

        val timeParts = StringBuilder()
        if (hideSeconds) {
            if (duration.hour > 0 || duration.minute > 0) {
                timeParts.append(formatHm(duration))
            }
        } else {
            if (duration.hour > 0 || duration.minute > 0 || duration.second > 0) {
                timeParts.append(formatHms(duration))
            }

            if (duration.millis > 0) {
                if (span > SECOND.totalMillis && timeParts.isEmpty()) {
                    // show seconds even on axis start - otherwise it looks strange
                    timeParts.append(formatHms(duration))
                }

                if (timeParts.isNotEmpty()) {
                    timeParts.append(".")
                }

                if (duration.millis % 10 == 0L && duration.millis % 100 == 0L) {
                    timeParts.append(duration.millis / 100)
                } else {
                    timeParts.append(duration.millis)
                }
            }
        }
        timeParts.toString().takeIf(String::isNotBlank)?.let(parts::add)

        val sign = "-".takeIf { v < 0 } ?: ""
        return parts.joinToString(prefix = sign, separator = " ")
    }

    private fun computeNiceTicks(): List<Double> {
        val niceTickInterval = listOf(
            1 * SECOND.totalMillis, 5 * SECOND.totalMillis, 15 * SECOND.totalMillis, 30 * SECOND.totalMillis,
            1 * MINUTE.totalMillis, 5 * MINUTE.totalMillis, 15 * MINUTE.totalMillis, 30 * MINUTE.totalMillis,
            1 * HOUR.totalMillis, 3 * HOUR.totalMillis, 6 * HOUR.totalMillis, 12 * HOUR.totalMillis,
            1 * DAY.totalMillis, 2 * DAY.totalMillis,
            1 * WEEK.totalMillis,
            4 * WEEK.totalMillis, // ~1 month
            12 * WEEK.totalMillis, // ~3 months
            48 * WEEK.totalMillis, // ~1 year
        ).minByOrNull { abs(it - targetStep.toLong()) } ?: SECOND.totalMillis

        var tick = ceil(normalStart / niceTickInterval) * niceTickInterval
        val result = ArrayList<Double>()
        while (tick <= normalEnd) {
            result.add(tick)
            tick += niceTickInterval
        }
        return result
    }

    companion object {
//        private val dayFormat = newStringFormat("{d}d")
//        private val hmsFormat = newStringFormat("{d}:{02d}:{02d}")
//        private val hmFormat = newStringFormat("{d}:{02d}")

//        private fun formatTotalDays(duration: Duration) = dayFormat.apply(duration.totalDays)
//        private fun formatHms(duration: Duration) = hmsFormat.apply(duration.hour, duration.minute, duration.second)
//        private fun formatHm(duration: Duration) = hmFormat.apply(duration.hour, duration.minute)

        private fun newStringFormat(format: String, tz: TimeZone?): StringFormat =
            StringFormat.forNArgs(format, -1, tz = tz)

        private fun StringFormat.apply(vararg args: Any): String = format(args.toList())
    }
}
