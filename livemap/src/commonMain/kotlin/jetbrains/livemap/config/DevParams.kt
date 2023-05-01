/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.livemap.core.layers.RenderTarget

class DevParams(private val devParams: Map<*, *>) {

    companion object {
        val SHOW_RESET_POSITION_ACTION = BoolParam("show_reset_position_action", true)

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

        val RENDER_TARGET: EnumParam<RenderTarget> =
            EnumParam(
                "render_target",
                RenderTarget.OWN_OFFSCREEN_CANVAS,
                listOf(
                    Pair("offscreen_layers", RenderTarget.OWN_OFFSCREEN_CANVAS),
                    Pair("screen_layers", RenderTarget.OWN_SCREEN_CANVAS)
                )
            )

        val MICRO_TASK_EXECUTOR: EnumParam<MicroTaskExecutor> =
            EnumParam(
                "microtask_executor", MicroTaskExecutor.AUTO,
                listOf(
                    Pair("ui_thread", MicroTaskExecutor.UI_THREAD),
                    Pair("background", MicroTaskExecutor.BACKGROUND),
                    Pair("auto", MicroTaskExecutor.AUTO)
                )
            )
    }

    enum class MicroTaskExecutor {
        UI_THREAD,
        BACKGROUND,
        AUTO
    }

    fun isSet(param: BoolParam): Boolean = param.isSet(this)
    fun read(param: IntParam): Int = param.read(this)
    fun read(param: DoubleParam): Double = param.read(this)
    fun read(param: StringParam): String = param.read(this)
    fun <ValueT : Enum<*>> read(param: EnumParam<ValueT>): ValueT = param.read(this)
    private operator fun get(key: String): Any? = devParams[key]

    class StringParam(val key: String, private val defaultValue: String) {

        fun read(params: DevParams): String =
            when(val v = params[key]) {
                null -> defaultValue
                is String -> v
                else -> error("")
            }
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
}