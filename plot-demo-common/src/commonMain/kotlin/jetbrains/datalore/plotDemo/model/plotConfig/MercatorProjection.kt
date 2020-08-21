/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

open class MercatorProjection : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            polygon_noProjection(),
            polygon_Mercator(),
            polygon_Mercator_ZoomOut_Y(),
            polygon_Mercator_ZoomOut_X(),
            polygon_Mercator_ZoomIn_Y()
        )
    }

    companion object {
        private val unitPolygon = """
            {
                "lon" : [25, 25, 26, 26],
                "lat" : [69, 70, 70, 69]
            }
        """.trimIndent()

        private val spec = parsePlotSpec(
            """
            {
                'kind': 'plot',
                'data': $unitPolygon,
                'layers': [
                    {
                        'geom': 'polygon',
                        'mapping': {
                            'x': 'lon',
                            'y': 'lat'
                        }    
                    }
                ],
                'ggtitle': { 'text': 'coord_fixed (no projection)' },
                'coord': { 'name': 'fixed' }
            }
            """.trimIndent()
        )

        @Suppress("FunctionName")
        fun polygon_noProjection(): Map<String, Any> {
            spec["coord"] = mapOf("name" to "fixed")
            spec["ggtitle"] = mapOf("text" to "coord_fixed (no projection)")
            return HashMap(spec)
        }

        @Suppress("FunctionName")
        fun polygon_Mercator(): Map<String, Any> {
            spec["coord"] = mapOf("name" to "map")
            spec["ggtitle"] = mapOf("text" to "coord_map (Mercator)")
            return HashMap(spec)
        }

        @Suppress("FunctionName")
        fun polygon_Mercator_ZoomOut_Y(): Map<String, Any> {
            spec["coord"] = coord_map_spec(yLim = ClosedRange(64.0, 75.0))
            spec["ggtitle"] = mapOf("text" to "Zoom-out Y")
            return HashMap(spec)
        }

        @Suppress("FunctionName")
        fun polygon_Mercator_ZoomOut_X(): Map<String, Any> {
            spec["coord"] = coord_map_spec(xLim = ClosedRange(15.0, 35.0))
            spec["ggtitle"] = mapOf("text" to "Zoom-out X")
            return HashMap(spec)
        }

        @Suppress("FunctionName")
        fun polygon_Mercator_ZoomIn_Y(): Map<String, Any> {
            spec["coord"] = coord_map_spec(yLim = ClosedRange(69.2, 69.8))
            spec["ggtitle"] = mapOf("text" to "Zoom-in Y")
            return HashMap(spec)
        }

        @Suppress("FunctionName")
        private fun coord_map_spec(xLim: ClosedRange<Double>? = null, yLim: ClosedRange<Double>? = null): Map<*, *> {
            val map = mutableMapOf<String, Any>("name" to "map")
            xLim?.run { map["xlim"] = listOf(lowerEnd, upperEnd) }
            yLim?.run { map["ylim"] = listOf(lowerEnd, upperEnd) }
            return map
        }
    }
}
