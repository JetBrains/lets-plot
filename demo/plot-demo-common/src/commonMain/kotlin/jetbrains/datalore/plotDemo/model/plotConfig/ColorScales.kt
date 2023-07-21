/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import org.jetbrains.letsPlot.core.spec.Option.Scale.DIRECTION
import org.jetbrains.letsPlot.core.spec.Option.Scale.HUE_RANGE
import org.jetbrains.letsPlot.core.spec.Option.Scale.START_HUE
import demoAndTestShared.parsePlotSpec

open class ColorScales {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            color_gradient(),
            color_gradient_blue_red(),

            color_gradient2(),
            color_gradient2_midpoint(),
            color_gradient2_rainbow(),

            color_hue(0.0, 360.0),
            color_hue(90.0, 180.0),
            color_hue(90.0, 180.0, dir = -1),
            color_hue(0.0, 360.0, 180.0),
//            color_hue_discrete(90.0, 360.0),
//            color_hue_discrete(90.0, 360.0, -1),
            color_hue_discrete(15.0, 375.0),
            color_hue_discrete(15.0, 375.0, -1),

            color_hue_chroma(),
            color_hue_luminance(),

            color_grey(),
            color_grey_white2black(),

            color_brewer(),
            color_brewer_direction(),
            color_brewer_diverging(),
            color_brewer_qualitative(),
            color_brewer_qualitative_paired(),

            color_brewer_discrete_def(2),
            color_brewer_discrete_def(4),
            color_brewer_discrete_def(8),
            color_brewer_discrete_def(12),
            color_brewer_discrete_def(32),

            color_manual()
        )
    }


    companion object {

        private fun scaleParamsAsJsonPart(map: Map<String, Any>): String {
            val sb = StringBuilder()
            for (key in map.keys) {
                sb.append(',').append('"').append(key).append('"').append(':')
                val v = map.getValue(key)
                sb.append(toJsonValue(v))
            }
            return sb.toString()
        }

        private fun toJsonValue(v: Any): String {
            val sb = StringBuilder()
            when (v) {
                is List<*> -> {
                    sb.append('[')
                    for (o in v) {
                        if (sb.length > 1) {
                            sb.append(',')
                        }
                        sb.append(toJsonValue(o!!))
                    }
                    sb.append(']')
                }
                is String -> sb.append('"').append(v.toString()).append('"')
                else -> sb.append(v.toString())
            }
            return sb.toString()
        }

        private fun specWithDiscreteData(
            dataCount: Int,
            scale_mapper_kind: String,
            scaleParams: Map<String, Any> = emptyMap()
        ): String {
            val paramsCopy = HashMap(scaleParams)
            paramsCopy["scale_mapper_kind"] = scale_mapper_kind

            val data = (0 until dataCount).map { "s$it" }.toList()

            return specWithColorScale(data, paramsCopy)
        }

        private fun specWithContinuousData(
            fromX: Int,
            toX: Int,
            scale_mapper_kind: String,
            scaleParams: Map<String, Any> = emptyMap()
        ): String {
            val paramsCopy = HashMap(scaleParams)
            paramsCopy["scale_mapper_kind"] = scale_mapper_kind
            return specWithColorScale((fromX..toX).toList(), paramsCopy)
        }

        private fun specWithColorScale(data: List<*>, scaleParams: Map<String, Any>): String {
            return "{" +
                    "   'kind': 'plot'," +
                    "   'data': {'x':" + toJsonValue(
                data
            ) + "}," +
                    "   'mapping': {'x':'x', 'color':'x', 'fill':'x'}," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'tile'," +
                    "                  'width': 1.0," +
                    "                  'height': 10.0" +
                    "               }" +
                    "           ]," +
                    "   'scales': [" +
                    "               {" +
                    "                  'aesthetic': 'fill'" + scaleParamsAsJsonPart(
                scaleParams
            ) +
                    "               }," +
                    "               {" +
                    "                  'aesthetic': 'color'" + scaleParamsAsJsonPart(
                scaleParams
            ) +
                    "               }" +
                    "           ]" +
                    "}"
        }


//
// =======================
//

        @Suppress("FunctionName")
        private fun color_gradient(): MutableMap<String, Any> {
            val spec = specWithContinuousData(
                0,
                128,
                "color_gradient"
            )
            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_gradient_blue_red(): MutableMap<String, Any> {
            val params = mapOf(
                "low" to "blue",
                "high" to "red"
            )
            val spec = specWithContinuousData(
                0,
                128,
                "color_gradient",
                params
            )
            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_gradient2(): MutableMap<String, Any> {
            val spec = specWithContinuousData(
                -64,
                64,
                "color_gradient2"
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_gradient2_midpoint(): MutableMap<String, Any> {
            val params = mapOf(
                "midpoint" to 10.0
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_gradient2",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_gradient2_rainbow(): MutableMap<String, Any> {
            val params = mapOf(
                "low" to "green",
                "mid" to "yellow",
                "high" to "red"
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_gradient2",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_hue(h0: Double, h1: Double, start: Double = 0.0, dir: Int = 1): MutableMap<String, Any> {
            val params = mapOf(
                HUE_RANGE to listOf(h0, h1),
                START_HUE to start,
                DIRECTION to dir
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_hue_discrete(h0: Double, h1: Double, dir: Int = 1): MutableMap<String, Any> {
            val params = mapOf(
                HUE_RANGE to listOf(h0, h1),
                DIRECTION to dir
            )
            val spec = specWithDiscreteData(
                32,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_hue_chroma(): MutableMap<String, Any> {
            val params = mapOf(
                "c" to 20
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_hue_luminance(): MutableMap<String, Any> {
            val params = mapOf(
                "l" to 40
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_grey(): MutableMap<String, Any> {
            val spec = specWithContinuousData(
                -64,
                64,
                "color_grey"
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_grey_white2black(): MutableMap<String, Any> {
            val params = mapOf(
                "start" to 1.0,
                "end" to 0
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_grey",
                params
            )

            return parsePlotSpec(spec)
        }


        @Suppress("FunctionName")
        private fun color_brewer(): MutableMap<String, Any> {
            val spec = specWithContinuousData(
                -64,
                64,
                "color_brewer"
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_brewer_direction(): MutableMap<String, Any> {
            val params = mapOf(
                "direction" to -1
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_brewer_diverging(): MutableMap<String, Any> {
            val params = mapOf(
                "type" to "div"
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_brewer_qualitative(): MutableMap<String, Any> {
            val params = mapOf(
                "type" to "qual"
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_brewer_qualitative_paired(): MutableMap<String, Any> {
            val params = mapOf(
                "type" to "qual",
                "palette" to "Paired"
            )
            val spec = specWithContinuousData(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun color_brewer_discrete_def(count: Int): MutableMap<String, Any> {
            val params = emptyMap<String, Any>()
            val spec = specWithDiscreteData(
                count,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }


        @Suppress("FunctionName")
        private fun color_manual(): MutableMap<String, Any> {
            val params = mapOf(
                "values" to listOf(
                    "#999999",
                    "#E69F00",
                    "#56B4E9",
                    "#009E73",
                    "#F0E442",
                    "#0072B2",
                    "#D55E00",
                    "#CC79A7"
                )
            )
            val spec = specWithColorScale((-8..8).toList(), params)
            return parsePlotSpec(spec)
        }

    }
}
