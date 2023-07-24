/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec
import demo.plot.common.model.SharedPieces

class TooltipWithSampling {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic()
        )
    }

    companion object {
        fun basic(): MutableMap<String, Any> {
            val spec = """
{
    "kind": "plot",
    "mapping": {
        "x": "x",
        "y": "y"
    },
    "layers": [
    {
        "geom": "polygon",
        "mapping": {
            "fill": "value",
            "group": "id"
        }
    },
    {
        "geom": "line",
        "mapping": {
            "color": "value",
            "group": "id"
        },
        "sampling": { "name": "pick", "n": 6 }
    }
    ]
}
"""

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = SharedPieces.samplePolygons()
            return plotSpec
        }
    }
}