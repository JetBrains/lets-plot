/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.datetime.Duration
import jetbrains.datalore.base.datetime.Duration.Companion.DAY
import jetbrains.datalore.base.datetime.Duration.Companion.HOUR
import jetbrains.datalore.base.datetime.Duration.Companion.MINUTE
import jetbrains.datalore.base.datetime.Duration.Companion.SECOND
import jetbrains.datalore.base.datetime.Duration.Companion.WEEK
import jetbrains.datalore.base.datetime.Duration.Companion.day
import jetbrains.datalore.base.datetime.Duration.Companion.hour
import jetbrains.datalore.base.datetime.Duration.Companion.millis
import jetbrains.datalore.base.datetime.Duration.Companion.minute
import jetbrains.datalore.base.datetime.Duration.Companion.second
import jetbrains.datalore.base.datetime.Duration.Companion.week
import jetbrains.datalore.base.stringFormat.StringFormat
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
                val hideSeconds = ticks.all { it >= DAY.duration || Duration(it.toLong()).second == 0 }
                ticks.map { formatString(it.toLong(), hideSeconds = hideSeconds) }
            }
        }
    }

    private fun formatString(v: Long, hideSeconds: Boolean = false): String {
        val sign = "-".takeIf { v < 0 } ?: ""
        val duration = Duration(abs(v))

        val parts = mutableListOf<String>()
        when {
            duration.week > 0 && duration.day > 0 -> parts.add(formatWeekAndDay(duration))
            duration.week > 0 && duration.day == 0 -> parts.add(formatWeek(duration))
            duration.week == 0 && duration.day > 0 -> parts.add(formatDay(duration))
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
                    timeParts.append(formatHms(duration))
                }

                if (timeParts.isNotEmpty()) {
                    timeParts.append(".")
                }

                timeParts.append(duration.millis)
            }
        }
        timeParts.toString().takeIf(String::isNotBlank)?.let(parts::add)

        return when {
            parts.isEmpty() -> "0"
            else -> parts.joinToString(prefix = sign, separator = " ")
        }
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
        private val weekAndDayFormat = newStringFormat("{d}W-{d}D")
        private val weekFormat = newStringFormat("{d}W")
        private val dayFormat = newStringFormat("{d}D")
        private val hmsFormat = newStringFormat("{02d}:{02d}:{02d}")
        private val hmFormat = newStringFormat("{02d}:{02d}")

        private fun formatWeekAndDay(duration: Duration) = weekAndDayFormat.apply(duration.week, duration.day)
        private fun formatWeek(duration: Duration) = weekFormat.apply(duration.week)
        private fun formatDay(duration: Duration) = dayFormat.apply(duration.day)
        private fun formatHms(duration: Duration) = hmsFormat.apply(duration.hour, duration.minute, duration.second)
        private fun formatHm(duration: Duration) = hmFormat.apply(duration.hour, duration.minute)

        private fun newStringFormat(format: String): StringFormat = StringFormat.forNArgs(format, -1)
        private fun StringFormat.apply(vararg args: Any): String = format(args.toList())
    }
}
