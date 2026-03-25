/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.scale

import demoAndTestShared.parsePlotSpec
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class DateTimeScaleX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot("hours", 1.hours),
            plot("days", 1.days)
        )
    }

    companion object {
        private val startMillis = LocalDateTime(2003, 2, 1, 0, 0)
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds()

        fun plot(title: String, period: Duration): MutableMap<String, Any> {
            val n = 30

            val rnd = Random(0)
            val time = (0..n).map { startMillis + it * period.inWholeMilliseconds }.joinToString()
            val values = (0..n).map { rnd.nextDouble(0.0, 20.0) }.joinToString()
            val data = "   {" +
                    "      'time': [$time]," +
                    "      'values': [$values]" +
                    "   }"
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + data +
                    "           ," +
                    "   'scales': [" +
                    "               {" +
                    "                 'name': 'Time($title)'," +
                    "                 'aesthetic': 'x'," +
                    "                 'datetime': true" +
                    "               }" +
                    "             ]," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'y': 'values'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                 'geom': 'line'" +
                    "               }" +
                    "           ]" +
                    "}"

            return parsePlotSpec(spec)
        }
    }
}
