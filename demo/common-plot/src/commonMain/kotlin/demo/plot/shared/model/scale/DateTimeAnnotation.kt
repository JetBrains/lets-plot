/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.scale

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.random.Random

class DateTimeAnnotation {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot(Duration.DAY),
            colorMapping(),
            yminAndYmaxMapping()
        )
    }

    companion object {
        private val TZ = TimeZone.UTC

        fun plot(period: Duration): MutableMap<String, Any> {
            val instant = DateTime(Date(1, Month.FEBRUARY, 2003))
                .toEpochMilliseconds(TZ)

            val rnd = Random(0)

            val n = 30
            val time = (0..n).map { instant + it * period.duration }
            val values = (0..n).map { rnd.nextDouble(0.0, 20.0) }

            val spec = """
                {
                  'kind': 'plot',
                  'data': {
                     'time':   [ ${time.joinToString()} ],
                     'values': [ ${values.joinToString()} ]
                  },
                  'data_meta' : {
                    'series_annotations': [ 
                      {
                        'column': 'time',
                        'type': 'datetime'
                      }
                    ]
                  },
                  'mapping': {
                    'x': 'time',
                    'y': 'values'
                  },
                  'layers': [
                    {
                      'geom': 'line'
                    }
                  ]
                }""".trimIndent()

            return parsePlotSpec(spec)
        }

        fun colorMapping(): MutableMap<String, Any> {
            val spec = """
                {
                    "kind": "plot",
                     "ggtitle": { "text": "Color datetime mapping" },
                    "data": {
                        "val": [1.0, 2.0, 3.0, 4.0, 5.0], 
                        "days": [1609459200000, 1614038400000, 1617408000000, 1620086400000, 1633392000000]
                    }, 
                    "layers": [
                        { "geom": "bar", "mapping": { "x": "days", "color": "days" } }
                    ], 
                    "data_meta": { 
                        "series_annotations": [{"column": "days", "type": "datetime"}]
                    }
                }
            """.trimIndent()

            return parsePlotSpec(spec)
        }

        fun yminAndYmaxMapping(): MutableMap<String, Any> {
            val instant = DateTime(Date(1, Month.FEBRUARY, 2003))
                .toEpochMilliseconds(TZ)
            val errDuration = 7 * Duration.DAY.duration.toDouble()
            val rnd = Random(0)

            val n = 7
            val t1 = (0..n).map { instant + rnd.nextDouble(errDuration) }
            val t2 = (0..n).map { instant - rnd.nextDouble(errDuration) }
            val v = (0..n).map { "\"${Char('a'.code + it)}\"" }.toList()

            val spec = """
            |{
            |   "kind": "plot", 
            |   "data": {
            |       "val": [${v.joinToString(", ")}], 
            |       "t1": [${t1.joinToString(", ")}], 
            |       "t2": [${t2.joinToString(", ")}]
            |   }, 
            |   "layers": [
            |       {
            |           "mapping": { "x": "val", "ymin": "t1", "ymax": "t2"}, 
            |           "stat": "identity", 
            |           "position": "identity", 
            |           "geom": "linerange",
            |           "size": 2
            |       }
            |   ], 
            |   "data_meta": {
            |       "series_annotations": [
            |           {"column": "t1", "type": "datetime"}, 
            |           {"column": "t2", "type": "datetime"}
            |       ]
            |   }
            |}
            """.trimMargin()

            return parsePlotSpec(spec)
        }
    }
}
