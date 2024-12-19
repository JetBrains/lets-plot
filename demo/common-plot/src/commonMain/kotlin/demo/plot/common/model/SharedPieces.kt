/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

object SharedPieces {

    @Suppress("FunctionName")
    fun rasterData_Blue(): Map<String, List<*>> {
        val x = ArrayList<Double>()
        val y = ArrayList<Double>()
        val fill = ArrayList<Double>()
        val alpha = ArrayList<Double>()

        val width = 3
        val height = 3
        for (col in 0 until width) {
            for (row in 0 until height) {
                x.add(col.toDouble())
                y.add(row.toDouble())
                fill.add(col.toDouble())
                alpha.add(row.toDouble())
            }
        }

        @Suppress("DuplicatedCode")
        val map = HashMap<String, List<*>>()
        map["x"] = x
        map["y"] = y
        map["fill"] = fill
        map["alpha"] = alpha
        return map
    }

    @Suppress("FunctionName")
    fun rasterData_RGB(): Map<String, List<*>> {
        //  R  |  G  |  B    alpha = 1
        //  R  |  G  |  B    alpha = 0.5
        // .5  |  1  |  .5   <-- gray, alpha

        val fillInt = intArrayOf(0xFF0000, 0xFF00, 0xFF, 0xFF0000, 0xFF00, 0xFF, 0x7F0000, 0x7F00, 0x7F)

        val fill = ArrayList<Double>()
        for (i in fillInt) {
            fill.add(i.toDouble())
        }

        val alpha = listOf(1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 1.0, 0.5)
        val x = listOf(0.0, 1.0, 2.0, 0.0, 1.0, 2.0, 0.0, 1.0, 2.0)
        val y = listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0)

        val map = HashMap<String, List<*>>()
        map["x"] = x
        map["y"] = y
        map["fill"] = fill
        map["alpha"] = alpha
        return map
    }

    fun sampleImageDataUrl3x3(): String {
        // 3 x 3 square with alpha
        return "data:image/png;base64," + "iVBORw0KGgoAAAANSUhEUgAAAAMAAAADCAYAAABWKLW/AAAALUlEQVQYV2MU0nb+L28fxaDiHsnAKKTj3CNvF8WgCuKIaLpLytsGMci7xzAAAKIrB5V5IK5KAAAAAElFTkSuQmCC"
    }

    fun samplePolygons(): Map<String, List<*>> {
        val ids = listOf("1.1", "2.1", "1.2", "2.2", "1.3", "2.3")
        val values = listOf(3.0, 3.1, 3.1, 3.2, 3.15, 3.5)

        val x = listOf(
            2.0, 1.0, 1.1, 2.2, 1.0, 0.0, 0.3, 1.1, 2.2, 1.1, 1.2, 2.5, 1.1, 0.3,
            0.5, 1.2, 2.5, 1.2, 1.3, 2.7, 1.2, 0.5, 0.6, 1.3
        )
        val y = listOf(
            -0.5, 0.0, 1.0, 0.5, 0.0, 0.5, 1.5, 1.0, 0.5, 1.0, 2.1, 1.7, 1.0, 1.5,
            2.2, 2.1, 1.7, 2.1, 3.2, 2.8, 2.1, 2.2, 3.3, 3.2
        )

        val idsRep = ArrayList<String>()
        val valueRep = ArrayList<Double>()
        for (i in ids.indices) {
            val id = ids[i]
            val v = values[i]
            for (j in 0..3) {
                idsRep.add(id)
                valueRep.add(v)
            }
        }

        val map = HashMap<String, List<*>>()
        map["id"] = idsRep
        map["x"] = x
        map["y"] = y
        map["value"] = valueRep
        return map
    }

    fun samplePointsSpec(): String {

        return "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }" +
                "," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'" +
                "                           }" +
                "               }" +
                "           ]"
    }

    fun samplePolyAndPointsPlotWith(
        addedLayerSpecJson: String,
        addedData: Map<String, List<*>>
    ): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var addedLayerSpecJson = addedLayerSpecJson
        if (addedData.isNotEmpty()) {
            val layerSpec = parsePlotSpec(addedLayerSpecJson)
            layerSpec["data"] = addedData
            addedLayerSpecJson = JsonSupport.formatJson(layerSpec)
        }

        val spec = "{" +
                "   'kind': 'plot'," +
                samplePolyAndPoints(addedLayerSpecJson) +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        val data = samplePolygons()
        //data.putAll(addedData);
        plotSpec["data"] = data
        return plotSpec
    }

    private fun samplePolyAndPoints(addedLayerSpec: String?): String {
        return "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom': 'polygon'," +
                "                  'mapping': {" +
                "                               'fill': 'id'," +
                "                               'group': 'id'" +
                "                              }" +
                "               },{" +
                "                  'geom': 'point'," +
                "                  'alpha': '0.5'," +
                "                  'x': '0.'," +
                "                  'y': '0.'," +
                "                  'color': 'red'," +
                "                  'size': '5'" +
                "               },{" +
                "                  'geom': 'point'," +
                "                   'alpha': '0.5'," +
                "                   'x': '1.'," +
                "                   'y': '1.'," +
                "                   'color': 'green'," +
                "                   'size': '5'" +
                "               }" +
                (if (addedLayerSpec == null)
                    ""
                else
                    ", $addedLayerSpec") +
                "           ]" +
                ""
    }
}
