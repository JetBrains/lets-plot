/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.scale

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.tz.TimeZone.Companion.UTC
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import kotlin.random.Random

open class DateTimeScaleX : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            plot("hours", hour),
            plot("days", day)
        )
    }

    companion object {
        private const val second = 1000.0
        private const val minute = 60.0 * second
        private const val hour = 60.0 * minute
        private const val day = 24.0 * hour
        private val instant = UTC.toInstant(DateTime(Date(1, Month.FEBRUARY, 2003)))

        fun plot(title: String, timeScale: Double): Map<String, Any> {
            val n = 30

            val rnd = Random(0)
            val time = (0..n).map { instant.timeSinceEpoch + it * timeScale }.joinToString()
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
