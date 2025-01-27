/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec


// fun ggbunch()
class GGBunchNew {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plotBunch(),
            plotBunchAutoLayout()
        )
    }

    private fun plotBunch(): MutableMap<String, Any> {
        val spec = """
        |{
        |   'kind': 'subplots',
        |   'layout': {
        |     'name': 'free',
        |     'regions': [
        |       [0, 0, 0.5, 0.5],
        |       [0.5, 0, 0.5, 0.5],
        |       [0, 0.5, 0.5, 0.5]
        |     ]
        |   },
        |   'figures': [
        |                 ${onePlotSpecStr("blue")},
        |                 ${onePlotSpecStr("red")},
        |                 ${onePlotSpecStr("magenta")}
        |            ],
        |   'ggtitle': {
        |     'text': 'This is the Title',
        |     'subtitle': 'This is the subtitle'
        |   },
        |   'caption': {
        |     'text': 'This is the caption'
        |   }
        |}
        """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun plotBunchAutoLayout(): MutableMap<String, Any> {
        val spec = """
        |{
        |   'kind': 'subplots',
        |   'layout': {
        |     'name': 'free',
        |     'regions': [
        |       [0, 0, 0.5, 0.5]
        |     ]
        |   },
        |   'figures': [
        |                 ${onePlotSpecStr("blue")},
        |                 ${onePlotSpecStr("red")},
        |                 ${onePlotSpecStr("magenta")}
        |            ]
        |}
        """.trimMargin()
        return parsePlotSpec(spec)
    }

    @Suppress("DuplicatedCode")
    private fun onePlotSpecStr(color: String): String {
        val spec = """
        |{
        |   'kind': 'plot',
        |   'theme': {'name': 'bw'},
        |   'data': {'x': [1, 2, 3], 'y': [0, 3, 1]},
        |   'mapping':  {
        |                   'x': 'x',
        |                   'y': 'y'
        |               },
        |   'layers':   [
        |                   {
        |                       'geom': 'point',
        |                       'color': '${color}'
        |                   }
        |               ]
        |}
        """.trimMargin()

        return spec
    }

}