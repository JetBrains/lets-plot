/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.livemap.core.rendering.layers.RenderTarget
import jetbrains.livemap.core.util.EasingFunction
import kotlin.math.pow

class DevParams(private val devParams: Map<*, *>) {

    companion object {
        val SCALABLE_SYMBOLS = BoolParam("scalable_symbols", false)
        val SCALABLE_SYMBOLS_MIN_FACTOR = DoubleParam("scalable_symbols_min_factor", 0.5)
        val SCALABLE_SYMBOLS_MAX_FACTOR = DoubleParam("scalable_symbols_max_factor", 2.5)
        val SCALABLE_SYMBOL_ZOOM_IN_EASING = EnumParam<Easing>(
            key = "scalable_symbol_zoom_in_easing",
            defaultValue = Easing.IN_QUAD,
            valuesMap = listOf(
                "IN_QUAD" to Easing.IN_QUAD,
                "IN_QUBIC" to Easing.IN_QUBIC,
                "IN_QUART" to Easing.IN_QUART,
                "OUT_QUAD" to Easing.OUT_QUAD,
                "OUT_QUBIC" to Easing.OUT_QUBIC,
                "OUT_QUART" to Easing.OUT_QUART,
            )
        )

        val SCALABLE_SYMBOL_ZOOM_OUT_EASING = EnumParam<Easing>(
            key = "scalable_symbol_zoom_out_easing",
            defaultValue = Easing.OUT_QUAD,
            valuesMap = listOf(
                "IN_QUAD" to Easing.IN_QUAD,
                "IN_QUBIC" to Easing.IN_QUBIC,
                "IN_QUART" to Easing.IN_QUART,
                "OUT_QUAD" to Easing.OUT_QUAD,
                "OUT_QUBIC" to Easing.OUT_QUBIC,
                "OUT_QUART" to Easing.OUT_QUART,
            )
        )

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
                RenderTarget.OWN_SCREEN_CANVAS,
                listOf(
                    Pair("offscreen_layers", RenderTarget.OWN_OFFSCREEN_CANVAS),
                    Pair("screen_layers", RenderTarget.OWN_SCREEN_CANVAS),
                    Pair("single_layer", RenderTarget.SINGLE_SCREEN_CANVAS)
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

    enum class Easing(val function: EasingFunction) {
        LINEAR({ x -> x }),
        IN_QUAD({ x -> x * x }),
        IN_QUBIC({ x -> x * x * x }),
        IN_QUART({ x -> x * x * x * x }),
        OUT_QUAD({ x -> 1 - (1 - x) * (1 - x) }),
        OUT_QUBIC({ x -> 1 - (1 - x).pow(3) }),
        OUT_QUART({ x -> 1 - (1 - x).pow(4) }),
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