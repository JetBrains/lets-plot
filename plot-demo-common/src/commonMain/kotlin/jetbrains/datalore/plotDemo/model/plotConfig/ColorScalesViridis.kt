/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_CMAP
import org.jetbrains.letsPlot.core.spec.Option.Scale.Viridis
import demoAndTestShared.parsePlotSpec

/**
 * For reference see https://cran.r-project.org/web/packages/viridis/vignettes/intro-to-viridis.html
 */
open class ColorScalesViridis {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            scaleViridis("viridis"),
            scaleViridisDiscrete("D"),
            scaleViridis("viridis", alpha = .5),
            scaleViridisDiscrete("viridis", alpha = .5),
            scaleViridis("viridis", begin = .5),
            scaleViridisDiscrete("viridis", begin = .5),
            scaleViridis("viridis", begin = .1, end = .7, dir = -1.0),
            scaleViridisDiscrete("viridis", begin = .1, end = .7, dir = -1.0),

            scaleViridis("magma"),
            scaleViridisDiscrete("A"),
            scaleViridis("inferno"),
            scaleViridisDiscrete("B"),
            scaleViridis("plasma"),
            scaleViridisDiscrete("C"),
            scaleViridis("cividis"),
            scaleViridisDiscrete("E"),
            scaleViridis("turbo"),
            scaleViridisDiscrete("turbo"),
            scaleViridis("twilight"),
            scaleViridisDiscrete("twilight"),
        )
    }


    companion object {

        private fun scaleViridis(
            cmapName: String,
            alpha: Double? = null,
            begin: Double? = null,
            end: Double? = null,
            dir: Double? = null
        ): MutableMap<String, Any> {
            val params = mapOf(
                Viridis.CMAP_NAME to cmapName,
                Viridis.ALPHA to alpha,
                Viridis.BEGIN to begin,
                Viridis.END to end,
                Viridis.DIRECTION to dir
            ).filterValues { it != null }.mapValues { (_, v) -> v as Any }

            val spec = specWithContinuousData(
                -64,
                64,
                scaleMapperKind = COLOR_CMAP,
                scaleParams = params
            )

            return parsePlotSpec(spec)
        }

        @Suppress("FunctionName")
        private fun scaleViridisDiscrete(
            cmapName: String,
            alpha: Double? = null,
            begin: Double? = null,
            end: Double? = null,
            dir: Double? = null
        ): MutableMap<String, Any> {
            val params = mapOf(
                Viridis.CMAP_NAME to cmapName,
                Viridis.ALPHA to alpha,
                Viridis.BEGIN to begin,
                Viridis.END to end,
                Viridis.DIRECTION to dir
            ).filterValues { it != null }.mapValues { (_, v) -> v as Any }

            val numColors = 5
            val spec = specWithDiscreteData(
                numColors,
                scaleMapperKind = COLOR_CMAP,
                scaleParams = params
            )

            return parsePlotSpec(spec)
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

        private fun specWithDiscreteData(
            dataCount: Int,
            scaleMapperKind: String,
            scaleParams: Map<String, Any> = emptyMap()
        ): String {
            val paramsCopy = HashMap(scaleParams)
            paramsCopy["scale_mapper_kind"] = scaleMapperKind

            val data = (0 until dataCount).map { "s$it" }.toList()

            return specWithColorScale(data, paramsCopy)
        }

        private fun specWithContinuousData(
            fromX: Int,
            toX: Int,
            scaleMapperKind: String,
            scaleParams: Map<String, Any> = emptyMap()
        ): String {
            val paramsCopy = HashMap(scaleParams)
            paramsCopy["scale_mapper_kind"] = scaleMapperKind
            return specWithColorScale((fromX..toX).toList(), paramsCopy)
        }

        private fun scaleParamsToLabel(scaleParams: Map<String, Any>): String {
            return ArrayList<String>().apply {
                add(scaleParams[Viridis.CMAP_NAME] as String)
                scaleParams[Viridis.ALPHA]?.let { add("alpha=$it") }
                scaleParams[Viridis.BEGIN]?.let { add("begin=$it") }
                scaleParams[Viridis.END]?.let { add("end=$it") }
                scaleParams[Viridis.DIRECTION]?.let { add("dir=$it") }
            }.joinToString()

        }

        private fun specWithColorScale(data: List<*>, scaleParams: Map<String, Any>): String {
            val label = scaleParamsToLabel(scaleParams)
            return "{" +
                    "   'kind': 'plot'," +
                    "   'data': {'x':" + toJsonValue(data) + "}," +
                    "   'mapping': {'x':'x', 'color':'x', 'fill':'x'}," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'tile'," +
                    "                  'width': 1.0," +
                    "                  'height': 5.0," +
                    "                  'show_legend': false" +
                    "               }" +
                    "           ]," +
                    "   'scales': [" +
                    "               {" +
                    "                  'aesthetic': 'fill'" + scaleParamsAsJsonPart(scaleParams) +
                    "               }," +
                    "               {" +
                    "                  'aesthetic': 'color'" + scaleParamsAsJsonPart(scaleParams) +
                    "               }," +
                    "               {" +
                    "                  'aesthetic': 'x', 'name': '$label'" +
                    "               }" +
                    "           ]," +
                    "   'coord': { 'name': 'cartesian' }" +
                    "}"
        }

    }
}
