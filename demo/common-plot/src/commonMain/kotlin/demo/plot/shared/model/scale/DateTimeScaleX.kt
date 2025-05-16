/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.scale

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone.Companion.UTC
import kotlin.random.Random

class DateTimeScaleX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot("hours", HOUR),
            plot("days", DAY)
        )
    }

    companion object {
        private const val SECOND = 1000.0
        private const val MINUTE = 60.0 * SECOND
        private const val HOUR = 60.0 * MINUTE
        private const val DAY = 24.0 * HOUR

        fun plot(title: String, timeScale: Double): MutableMap<String, Any> {
            val n = 30

            val instant = DateTime(Date(1, Month.FEBRUARY, 2003)).toInstant(UTC)

            val rnd = Random(0)
            val time = (0..n).map { instant.toEpochMilliseconds() + it * timeScale }.joinToString()
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
