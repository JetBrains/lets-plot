/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec


class GGBunch {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plotBunch()
        )
    }

    private fun plotBunch(): MutableMap<String, Any> {
        val spec = """
        |{
        |   'kind': 'ggbunch',
        |   'items': [
        |               {
        |                   'x': 0,
        |                   'y': 0,
        |                   'width': 150,
        |                   'height': 150,
        |                   'feature_spec': ${onePlotSpecStr("blue")} 
        |               },
        |               {
        |                   'x': 150,
        |                   'y': 0,
        |                   'width': 150,
        |                   'height': 150,
        |                   'feature_spec': ${onePlotSpecStr("red")} 
        |               },
        |               {
        |                   'x': 0,
        |                   'y': 150,
        |                   'width': 150,
        |                   'height': 150,
        |                   'feature_spec': ${onePlotSpecStr("magenta")} 
        |               }
        |            ]
        |}
        """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun onePlotSpecStr(color: String): String {
        val spec = """
        |{
        |   'kind': 'plot',
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