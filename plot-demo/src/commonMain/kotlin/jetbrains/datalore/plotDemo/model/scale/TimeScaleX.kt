/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.scale

import jetbrains.datalore.base.datetime.Duration
import jetbrains.datalore.base.datetime.Duration.Companion.HOUR
import jetbrains.datalore.base.datetime.Duration.Companion.MINUTE
import jetbrains.datalore.base.datetime.Duration.Companion.MS
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.random.Random

class TimeScaleX {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot("5 seconds", 0..5000 step 100, MS),
            plot("24 hours", 0..24, HOUR),
            plot("5 days", 0..120, HOUR),
            plot("30 days", 0..720, HOUR),
            plot("Negative time: -30 to 30 minutes", -30..30, MINUTE),
        )
    }

    companion object {
        fun plot(title: String, entries: Iterable<Int>, period: Duration): MutableMap<String, Any> {
            val rnd = Random(0)
            val time = entries.map { it * period.duration }
            val values = time.indices.map { rnd.nextDouble(0.0, 20.0) }

            val spec =
                """|{
                   |    "kind": "plot",
                   |    "data": {
                   |        "time": [${time.joinToString()}],
                   |        "values": [${values.joinToString()}]
                   |    },
                   |    "mapping": {"x": "time", "y": "values"},
                   |    "layers": [{"geom": "line"}],
                   |    "scales": [
                   |        {
                   |            "name": "$title",
                   |            "aesthetic": "x",
                   |            "time": true
                   |        }
                   |    ]
                   |}""".trimMargin()
            return parsePlotSpec(spec)
        }
    }
}
