/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.scale

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MS
import kotlin.random.Random

class TimeScaleX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot("5 seconds", 0..5000 step 25, MS),
            plot("24 hours", 0..24, HOUR),
            plot("24 hours", 0..24, HOUR, timeAxis = "y"),
            plot("5 days", 0..120, HOUR),
            plot("30 days", 0..720, HOUR),
            plot("-30..30, MINUTE", -30..30, MINUTE),
            plot("Special: no zero (12 to 30 minutes)", 12..30, MINUTE),
            plot("Special: assymetric range (-8 to 30 minutes)", -8..30, MINUTE),
            plot("Special: negative (-30..-20, MINUTE)", -30..-20, MINUTE),
            plot("Special: reversed negative (-20..-30, MINUTE)", -20 downTo -30, MINUTE),
        )
    }

    companion object {
        fun plot(
            title: String,
            entries: Iterable<Int>,
            period: Duration,
            timeAxis: String = "x"
        ): MutableMap<String, Any> {
            val rnd = Random(0)
            val time = entries.map { it * period.totalMillis }
            val values = time.indices.map { rnd.nextDouble(0.0, 20.0) }

            val mapping = when (timeAxis) {
                "x" -> """{"x": "time", "y": "values"}"""
                "y" -> """{"x": "values", "y": "time"}"""
                else -> throw IllegalStateException()
            }

            val spec =
                """|{
                   |    "kind": "plot",
                   |    "data": {
                   |        "time": [${time.joinToString()}],
                   |        "values": [${values.joinToString()}]
                   |    },
                   |    "mapping": $mapping,
                   |    "layers": [{"geom": "line"}],
                   |    "scales": [
                   |        {
                   |            "name": "$title",
                   |            "aesthetic": "$timeAxis",
                   |            "time": true
                   |        }
                   |    ]
                   |}""".trimMargin()
            return parsePlotSpec(spec)
        }
    }
}
