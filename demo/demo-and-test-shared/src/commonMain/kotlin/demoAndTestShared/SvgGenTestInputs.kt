/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

fun rawSpecSinglePlot(): MutableMap<String, Any> {
    val spec = """
            |{
            |  'kind': 'plot',
            |  'data': {'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']},
            |  'mapping': {
            |            'x': 'time',
            |            'y': '..count..'
            |          },
            |  'layers': [
            |              {
            |                 'geom': 'bar'
            |              }
            |          ]
            |}
        """.trimMargin()

    return parsePlotSpec(spec)
}

fun rawSpecGGBunch(): MutableMap<String, Any> {
    val spec = """
        |{
        |   'kind': 'ggbunch',
        |   'items': [
        |               {
        |                   'x': 0,
        |                   'y': 0,
        |                   'width': 150,
        |                   'height': 150,
        |                   'feature_spec': ${rawSpecStrGGBunchItemPlot()} 
        |               },
        |               {
        |                   'x': 150,
        |                   'y': 0,
        |                   'width': 150,
        |                   'height': 150,
        |                   'feature_spec': ${rawSpecStrGGBunchItemPlot()} 
        |               }
        |            ]
        |}
        """.trimMargin()
    return parsePlotSpec(spec)
}

fun rawSpecStrGGBunchItemPlot(): String {

    return """
        |{
        |   'kind': 'plot',
        |   'data': {'a': [1, 2], 'b': [0, 3]},
        |   'mapping':  {
        |                   'x': 'a',
        |                   'y': 'b'
        |               },
        |   'layers':   [
        |                   {
        |                       'geom': 'point'
        |                   }
        |               ]
        |}
        """.trimMargin()
}
