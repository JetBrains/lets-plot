/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.WEEK
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.hour
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.millis
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.minute
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.second
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.totalDays
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import kotlin.math.abs
import kotlin.math.ceil

class TimeBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int
) : BreaksHelperBase(rangeStart, rangeEnd, count) {
    override val breaks: List<Double>
    val formatter: (Any) -> String

    init {
        val ticks: List<Double> = when {
            targetStep < 1000 -> LinearBreaksHelper(rangeStart, rangeEnd, count).breaks
            else -> computeNiceTicks()
        }

        breaks = when (isReversed) {
            true -> ticks.reversed()
            false -> ticks
        }

        formatter = { v -> formatString((v as Number).toLong()) }
    }

    fun formatBreaks(ticks: List<Double>): List<String> {
        return when {
            targetStep < 1000 -> ticks.map(formatter)
            else -> {
                val hideSeconds = ticks.all { it >= DAY.duration || Duration(it.toLong()).second == 0L }
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
                if (span > SECOND.duration && timeParts.isEmpty()) {
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
        return  parts.joinToString(prefix = sign, separator = " ")
    }

    private fun computeNiceTicks(): List<Double> {
        val niceTickInterval = listOf(
            1 * SECOND.duration, 5 * SECOND.duration, 15 * SECOND.duration, 30 * SECOND.duration,
            1 * MINUTE.duration, 5 * MINUTE.duration, 15 * MINUTE.duration, 30 * MINUTE.duration,
            1 * HOUR.duration, 3 * HOUR.duration, 6 * HOUR.duration, 12 * HOUR.duration,
            1 * DAY.duration, 2 * DAY.duration,
            1 * WEEK.duration,
            4 * WEEK.duration, // ~1 month
            12 * WEEK.duration, // ~3 months
            48 * WEEK.duration, // ~1 year
        ).minByOrNull { abs(it - targetStep.toLong()) } ?: SECOND.duration

        var tick = ceil(normalStart / niceTickInterval) * niceTickInterval
        val result = ArrayList<Double>()
        while (tick <= normalEnd) {
            result.add(tick)
            tick += niceTickInterval
        }
        return result
    }

    companion object {
        private val dayFormat = newStringFormat("{d}d")
        private val hmsFormat = newStringFormat("{d}:{02d}:{02d}")
        private val hmFormat = newStringFormat("{d}:{02d}")

        private fun formatTotalDays(duration: Duration) = dayFormat.apply(duration.totalDays)
        private fun formatHms(duration: Duration) = hmsFormat.apply(duration.hour, duration.minute, duration.second)
        private fun formatHm(duration: Duration) = hmFormat.apply(duration.hour, duration.minute)

        private fun newStringFormat(format: String): StringFormat = StringFormat.forNArgs(format, -1)
        private fun StringFormat.apply(vararg args: Any): String = format(args.toList())
    }
}
