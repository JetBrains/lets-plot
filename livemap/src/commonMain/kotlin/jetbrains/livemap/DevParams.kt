/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.gis.tileprotocol.TileService.Theme
import jetbrains.livemap.core.rendering.layers.RenderTarget

class DevParams(private val devParams: Map<*, *>) {

    enum class MicroTaskExecutor {
        UI_THREAD,
        BACKGROUND,
        AUTO
    }

    fun isSet(param: BoolParam): Boolean {
        return param.isSet(this)
    }

    fun isNotSet(param: RasterParam): Boolean {
        return !param.isSet(this)
    }

    fun read(param: IntParam): Int {
        return param.read(this)
    }

    fun read(param: DoubleParam): Double {
        return param.read(this)
    }

    fun read(param: StringParam): String {
        return param.read(this)
    }

    fun read(param: RasterParam): RasterTiles? {
        return param.read(this)
    }

    fun read(param: VectorParam): VectorTiles {
        return param.read(this)
    }

    fun <ValueT : Enum<*>> read(param: EnumParam<ValueT>): ValueT {
        return param.read(this)
    }

    private operator fun get(key: String): Any? {
        return devParams[key]
    }

    class IntParam(val key: String, private val defaultValue: Int) {

        fun read(params: DevParams): Int =
            when(val v = params[key]) {
                null -> defaultValue
                is Number -> v.toInt()
                else -> error("")
            }
    }

    class DoubleParam(val key: String, private val defaultValue: Double) {

        fun read(params: DevParams): Double =
            when(val v = params[key]) {
                null -> defaultValue
                is Number -> v.toDouble()
                else -> throw IllegalArgumentException()
            }
    }

    class StringParam(val key: String, private val defaultValue: String) {

        fun read(params: DevParams): String =
            when(val v = params[key]) {
                null -> defaultValue
                is String -> v
                else -> throw IllegalArgumentException()
            }
    }

    class BoolParam(
        val key: String,
        private val defaultValue: Boolean
    ) {

        internal fun isSet(params: DevParams): Boolean =
            when(val v = params[key]) {
                null -> defaultValue
                is Boolean -> v
                is String -> v.toBoolean()
                else -> throw IllegalArgumentException()
            }
    }

    class RasterParam(val key: String) {
        fun read(params: DevParams): RasterTiles? {

            return when(val v = params[key]) {
                null -> null
                is Map<*, *> -> RasterTiles().apply {
                    v["host"]?.let { if (it is String) host = it }
                    v["port"]?.let { if (it is Int) port = it }
                    v["format"]?.let { if (it is String) format = it }
                }
                else -> throw IllegalArgumentException()
            }
        }

        fun isSet(params: DevParams): Boolean {
            return params[key] != null
        }
    }

    class RasterTiles {
        var host: String = "localhost"
        var port: Int? = null
        var format: String = "/\${z}/\${x}/\${y}.png"
    }

    class VectorParam(val key: String) {
        fun read(params: DevParams): VectorTiles {
            val vector = VectorTiles()

            return when(val v = params[key]) {
                null -> vector
                is Map<*, *> -> vector.apply {
                    v["host"]?.let { if (it is String) host = it }
                    v["port"]?.let { if (it is Int) port = it }
                    v["theme"]?.let { if (it is String) theme = parseTheme(it) }
                }
                else -> throw IllegalArgumentException()
            }
        }

        private fun parseTheme(theme: String): Theme {
            try {
                return Theme.valueOf(theme.toUpperCase())
            } catch (ignored: Exception) {
                throw IllegalArgumentException("Unknown theme type: $theme")
            }
        }
    }

    class VectorTiles {
        var host: String = "tiles.datalore.io"
        var port: Int? = null
        var theme: Theme = Theme.COLOR
    }

    class EnumParam<ValueT>(
        val key: String,
        private val defaultValue: ValueT,
        private val valuesMap: List<Pair<String, ValueT>>
    ) {

        private fun fromString(o: String): ValueT {
            valuesMap.forEach { (first, second) ->
                if (first.equals(o, ignoreCase = true)) {
                    return second
                }
            }

            throw IllegalArgumentException()
        }

        fun read(params: DevParams): ValueT =
            when(val v = params[key]) {
                null -> defaultValue
                is String -> fromString(v)
                else -> throw IllegalArgumentException()
            }
    }

    companion object {

        val PERF_STATS = BoolParam("perf_stats", false)
        val DEBUG_TILES = BoolParam("debug_tiles", false)
        val DEBUG_GRID = BoolParam("debug_grid", false)
        val TILE_CACHE_LIMIT = IntParam("tile_cache_limit", 36)
        val FRAGMENT_ACTIVE_DOWNLOADS_LIMIT = IntParam("fragment_active_downaloads_limit", 30)
        val FRAGMENT_CACHE_LIMIT = IntParam("fragment_cache_limit", 500)
        val COMPUTATION_PROJECTION_QUANT = IntParam("computation_projection_quant", 1000)
        val COMPUTATION_FRAME_TIME = IntParam("computation_frame_time", 28)
        val UPDATE_PAUSE_MS = IntParam("update_pause_ms", 0)
        val UPDATE_TIME_MULTIPLIER = DoubleParam("update_time_multiplier", 1.0)
        val POINT_SCALING = BoolParam("point_scaling", false)
        val RASTER_TILES = RasterParam("raster_tiles")
        val VECTOR_TILES = VectorParam("vector_tiles")

        val RENDER_TARGET: EnumParam<RenderTarget> = EnumParam(
            "render_target",
            RenderTarget.OWN_SCREEN_CANVAS,
            listOf(
                Pair("offscreen_layers", RenderTarget.OWN_OFFSCREEN_CANVAS),
                Pair("screen_layers", RenderTarget.OWN_SCREEN_CANVAS),
                Pair("single_layer", RenderTarget.SINGLE_SCREEN_CANVAS)
            )
        )

        val MICRO_TASK_EXECUTOR: EnumParam<MicroTaskExecutor> = EnumParam(
            "microtask_executor", MicroTaskExecutor.AUTO,
            listOf(
                Pair("ui_thread", MicroTaskExecutor.UI_THREAD),
                Pair("background", MicroTaskExecutor.BACKGROUND),
                Pair("auto", MicroTaskExecutor.AUTO)
            )
        )
    }
}