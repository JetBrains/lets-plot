/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil
import kotlin.math.abs

internal object DurationFormatter {
    val DEFAULT_DURATION_FORMATTER: (Any) -> String = { v ->
        formatDuration((v as Number).toLong())
    }

    private val dayFormat = newStringFormat("{d}d")
    private val hmsFormat = newStringFormat("{d}:{02d}:{02d}")
    private val hmFormat = newStringFormat("{d}:{02d}")

    private fun formatTotalDays(duration: Duration) = dayFormat.apply(duration.totalDays)
    private fun formatHms(duration: Duration) = hmsFormat.apply(duration.hour, duration.minute, duration.second)
    private fun formatHm(duration: Duration) = hmFormat.apply(duration.hour, duration.minute)

    fun formatBreaks(
        breaks: List<Double>,
        breakWidth: Double,
        span: Double,
        providedFormatter: ((Any) -> String)?,
    ): List<String> {
        return when {
            breakWidth < 1000 -> breaks.map(providedFormatter ?: DEFAULT_DURATION_FORMATTER)
            providedFormatter != null -> breaks.map(providedFormatter)
            else -> {
                // Use an improved version of the default formatter
                val hideSeconds = breaks.all { it >= DAY.totalMillis || Duration(it.toLong()).second == 0L }
                breaks.map { formatDuration(it.toLong(), hideSeconds = hideSeconds, span = span) }
            }
        }
    }

    fun formatDuration(v: Long, hideSeconds: Boolean = false, span: Double = 0.0): String {
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

    private fun newStringFormat(format: String): StringFormat =
        FormatterUtil.byPattern(format, tz = null)

    private fun StringFormat.apply(vararg args: Any): String = format(args.toList())
}