/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.scale

import demoAndTestShared.parsePlotSpec
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class TimeScaleX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot("5 seconds", 0..5000 step 25, 1.milliseconds),
            plot("24 hours", 0..24, 1.hours),
            plot("24 hours", 0..24, 1.hours, timeAxis = "y"),
            plot("5 days", 0..120, 1.hours),
            plot("30 days", 0..720, 1.hours),
            plot("-30..30, MINUTE", -30..30, 1.minutes),
            plot("Special: no zero (12 to 30 minutes)", 12..30, 1.minutes),
            plot("Special: assymetric range (-8 to 30 minutes)", -8..30, 1.minutes),
            plot("Special: negative (-30..-20, MINUTE)", -30..-20, 1.minutes),
            plot("Special: reversed negative (-20..-30, MINUTE)", -20 downTo -30, 1.minutes),
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
            val time = entries.map { it * period.inWholeMilliseconds }
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
