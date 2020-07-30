/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase
import kotlin.math.round
import kotlin.math.sign

open class AllColorScales : PlotConfigDemoBase(DoubleVector(600.0, 100.0)) {

    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            color_gradient(),
            color_gradient_blue_red(),

            color_gradient2(),
            color_gradient2_midpoint(),
            color_gradient2_rainbow(),

            color_hue(),
            color_hue_chroma(),
            color_hue_luminance(),

            color_grey(),
            color_grey_white2black(),

            color_brewer(),
            color_brewer_direction(),
            color_brewer_diverging(),
            color_brewer_qualitative(),
            color_brewer_qualitative_paired(),

            color_manual()
        )
    }


    companion object {

        private fun listOfInts(fromValue: Int, toValue: Int): List<Int> {
            val increment = round(sign((toValue - fromValue).toFloat())).toInt()
            val list = ArrayList<Int>()
            var i = fromValue
            while (i < toValue) {
                list.add(i)
                i += increment
            }
            return list
        }

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

        private fun specWithContinuousColors(
            fromX: Int,
            toX: Int,
            scale_mapper_kind: String,
            scaleParams: Map<String, Any> = emptyMap()
        ): String {
            val paramsCopy = HashMap(scaleParams)
            paramsCopy["scale_mapper_kind"] = scale_mapper_kind
            return specWithColorScale(
                fromX,
                toX,
                paramsCopy
            )
        }

        private fun specWithColorScale(fromX: Int, toX: Int, scaleParams: Map<String, Any>): String {
            return specWithColorScale(
                listOfInts(
                    fromX,
                    toX
                ), scaleParams
            )
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

        private fun color_gradient(): Map<String, Any> {
            val spec = specWithContinuousColors(
                0,
                128,
                "color_gradient"
            )
            return parsePlotSpec(spec)
        }

        private fun color_gradient_blue_red(): Map<String, Any> {
            val params = mapOf(
                "low" to "blue",
                "high" to "red"
            )
            val spec = specWithContinuousColors(
                0,
                128,
                "color_gradient",
                params
            )
            return parsePlotSpec(spec)
        }

        private fun color_gradient2(): Map<String, Any> {
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_gradient2"
            )

            return parsePlotSpec(spec)
        }

        private fun color_gradient2_midpoint(): Map<String, Any> {
            val params = mapOf(
                "midpoint" to 10.0
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_gradient2",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_gradient2_rainbow(): Map<String, Any> {
            val params = mapOf(
                "low" to "green",
                "mid" to "yellow",
                "high" to "red"
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_gradient2",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_hue(): Map<String, Any> {
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_hue"
            )

            return parsePlotSpec(spec)
        }

        private fun color_hue_chroma(): Map<String, Any> {
            val params = mapOf(
                "c" to 20
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_hue_luminance(): Map<String, Any> {
            val params = mapOf(
                "l" to 40
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_hue",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_grey(): Map<String, Any> {
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_grey"
            )

            return parsePlotSpec(spec)
        }

        private fun color_grey_white2black(): Map<String, Any> {
            val params = mapOf(
                "start" to 1.0,
                "end" to 0
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_grey",
                params
            )

            return parsePlotSpec(spec)
        }


        private fun color_brewer(): Map<String, Any> {
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_brewer"
            )

            return parsePlotSpec(spec)
        }

        private fun color_brewer_direction(): Map<String, Any> {
            val params = mapOf(
                "direction" to -1
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_brewer_diverging(): Map<String, Any> {
            val params = mapOf(
                "type" to "div"
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_brewer_qualitative(): Map<String, Any> {
            val params = mapOf(
                "type" to "qual"
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_brewer_qualitative_paired(): Map<String, Any> {
            val params = mapOf(
                "type" to "qual",
                "palette" to "Paired"
            )
            val spec = specWithContinuousColors(
                -64,
                64,
                "color_brewer",
                params
            )

            return parsePlotSpec(spec)
        }

        private fun color_manual(): Map<String, Any> {
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
            val spec =
                specWithColorScale(-8, 8, params)

            return parsePlotSpec(spec)
        }

    }
}
