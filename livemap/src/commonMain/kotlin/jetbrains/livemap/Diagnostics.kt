/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.MetricsService
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.core.rendering.layers.CanvasLayer
import jetbrains.livemap.core.rendering.layers.CanvasLayerComponent
import jetbrains.livemap.core.rendering.layers.LayersOrderComponent
import jetbrains.livemap.core.rendering.primitives.Label
import jetbrains.livemap.core.rendering.primitives.Text
import jetbrains.livemap.fragment.CachedFragmentsComponent
import jetbrains.livemap.fragment.DownloadingFragmentsComponent
import jetbrains.livemap.fragment.FragmentKey
import jetbrains.livemap.fragment.StreamingFragmentsComponent
import jetbrains.livemap.mapengine.basemap.raster.RasterTileLoadingSystem.HttpTileResponseComponent
import jetbrains.livemap.mapengine.basemap.vector.TileLoadingSystem.TileResponseComponent
import jetbrains.livemap.ui.UiService

open class Diagnostics {

    open fun update(dt: Long) {}

    class LiveMapDiagnostics(
        isLoading: Property<Boolean>,
        private val dirtyLayers: List<Int>,
        private val schedulerSystem: SchedulerSystem,
        private val debugService: MetricsService,
        private val uiService: UiService,
        private val registry: EcsComponentManager
    ) : Diagnostics() {

        private val diagnostics = ArrayList<Diagnostic>()

        private var deltaTime: Long = 0
        private val metrics: Text

        private val slowestSystemType: String?
            get() = debugService.slowestSystem()?.first!!::class.simpleName

        private val slowestSystemTime: Double
            get() = debugService.slowestSystem()?.second ?: 0.0

        init {
            diagnostics.addAll(
                listOf(
                    FreezingSystemDiagnostic(),
                    DirtyLayersDiagnostic(),
                    SlowestSystemDiagnostic(),
                    SchedulerSystemDiagnostic(),
                    FragmentsCacheDiagnostic(),
                    StreamingFragmentsDiagnostic(),
                    DownloadingFragmentsDiagnostic(),
                    DownloadingTilesDiagnostic(),
                    IsLoadingDiagnostic(isLoading)
                )
            )

            debugService.setValuesOrder(
                listOf(
                    TIMER_TICK,
                    SYSTEMS_UPDATE_TIME,
                    ENTITIES_COUNT,
                    SLOWEST_SYSTEM,
                    FREEZING_SYSTEM,
                    SCHEDULER_SYSTEM,
                    DIRTY_LAYERS,
                    STREAMING_FRAGMENTS,
                    DOWNLOADING_FRAGMENTS,
                    FRAGMENTS_CACHE,
                    DOWNLOADING_TILES,
                    IS_LOADING
                )
            )

            metrics = Text().apply {
                color = Color.DARK_GREEN
                fontFamily = "Courier New"
                fontSize = 12.0
            }

            val metricsLabel = Label(DoubleVector(0.0, 150.0), metrics).apply {
                padding = 4.0
                background = Color.WHITE
            }

            uiService.addRenderable(metricsLabel)
        }

        override fun update(dt: Long) {
            deltaTime = dt
            debugService.setValue(TIMER_TICK, "Timer tick: ${deltaTime.toOdd()}") // reduces excessive repaints
            debugService.setValue(
                SYSTEMS_UPDATE_TIME,
                "Systems update: ${debugService.totalUpdateTime.toOdd()}"
            )
            debugService.setValue(ENTITIES_COUNT, "Entities count: ${registry.entitiesCount}")

            diagnostics.forEach(Diagnostic::update)

            if (metrics.text != debugService.values) {
                metrics.text = debugService.values
                uiService.repaint()
            }
        }

        internal inner class FreezingSystemDiagnostic : Diagnostic {
            private var timeToShowLeft: Long = 0
            private var freezeTime = 0.0
            private var message = ""
            private val timeToShow = 7 * 1000

            override fun update() {
                if (slowestSystemTime > 16.0) {
                    if (slowestSystemTime > freezeTime) {
                        timeToShowLeft = timeToShow.toLong()
                        message = "Freezed by: ${formatDouble(slowestSystemTime, 1)} $slowestSystemType"
                        freezeTime = slowestSystemTime
                    }
                } else {
                    if (timeToShowLeft > 0) {
                        timeToShowLeft -= deltaTime
                    } else if (timeToShowLeft < 0) {
                        message = ""
                        timeToShowLeft = 0
                        freezeTime = 0.0
                    }
                }
                debugService.setValue(FREEZING_SYSTEM, message)
            }
        }

        internal inner class DirtyLayersDiagnostic : Diagnostic {
            override fun update() {
                val dirtyLayers = registry
                    .getEntitiesById(dirtyLayers)
                    .map { it.get<CanvasLayerComponent>().canvasLayer }
                    .toSet()
                    .intersect(registry.getSingleton<LayersOrderComponent>().canvasLayers)
                    .joinToString(transform = CanvasLayer::name)

                debugService.setValue(DIRTY_LAYERS, "Dirty layers: $dirtyLayers")
            }
        }

        internal inner class SlowestSystemDiagnostic : Diagnostic {

            override fun update() {
                debugService.setValue(
                    SLOWEST_SYSTEM,
                    "Slowest update: ${
                        if (slowestSystemTime > 2.0)
                            "${formatDouble(slowestSystemTime, 1)} $slowestSystemType"
                        else "-"
                    }"
                )
            }
        }

        internal inner class SchedulerSystemDiagnostic : Diagnostic {

            override fun update() {
                val tasksCount = registry.count<MicroThreadComponent>()
                debugService.setValue(SCHEDULER_SYSTEM, "Micro threads: $tasksCount, ${schedulerSystem.loading}")
            }
        }

        internal inner class FragmentsCacheDiagnostic : Diagnostic {

            override fun update() {
                val size = registry.tryGetSingleton<CachedFragmentsComponent>()?.keys()?.size ?: 0

                debugService.setValue(FRAGMENTS_CACHE, "Fragments cache: $size")
            }
        }

        internal inner class StreamingFragmentsDiagnostic : Diagnostic {

            override fun update() {
                val size = registry.tryGetSingleton<StreamingFragmentsComponent>()?.keys()?.size ?: 0
                debugService.setValue(STREAMING_FRAGMENTS, "Streaming fragments: $size")
            }
        }

        internal inner class DownloadingFragmentsDiagnostic : Diagnostic {

            override fun update() {
                val counts = registry.tryGetSingleton<DownloadingFragmentsComponent>()
                    ?.let { "D: ${it.downloading.size} Q: ${it.queue.values.sumOf(MutableSet<FragmentKey>::size)}" }
                    ?: "D: 0 Q: 0"

                debugService.setValue(DOWNLOADING_FRAGMENTS, "Downloading fragments: $counts")
            }
        }

        internal inner class DownloadingTilesDiagnostic : Diagnostic {
            @kotlinx.coroutines.ObsoleteCoroutinesApi
            override fun update() {
                val vector = registry
                    .getEntities(TileResponseComponent::class)
                    .filter { it.get<TileResponseComponent>().tileData == null }
                    .count()

                val raster = registry
                    .getEntities(HttpTileResponseComponent::class)
                    .filter { it.get<HttpTileResponseComponent>().imageData == null }
                    .count()

                debugService.setValue(DOWNLOADING_TILES, "Downloading tiles: V: $vector, R: $raster")
            }
        }

        internal inner class IsLoadingDiagnostic(private val isLoading: Property<Boolean>) : Diagnostic {

            override fun update() {
                debugService.setValue(IS_LOADING, "Is loading: ${isLoading.get()}")
            }
        }

        internal interface Diagnostic {
            @kotlinx.coroutines.ObsoleteCoroutinesApi
            fun update()
        }

        private fun formatDouble(v: Double, precision: Int): String {
            val intV = v.toInt()
            val fracV = ((v - intV.toDouble()) * 10.0 * precision.toDouble()).toInt()
            return "$intV.$fracV"
        }

        companion object {
            private const val TIMER_TICK = "timer_tick"
            private const val SYSTEMS_UPDATE_TIME = "systems_update_time"
            private const val ENTITIES_COUNT = "entities_count"

            private const val SLOWEST_SYSTEM = "slow_system"
            private const val FREEZING_SYSTEM = "freezing_system"
            private const val SCHEDULER_SYSTEM = "scheduler_load"
            private const val DIRTY_LAYERS = "dirty_layers"
            private const val STREAMING_FRAGMENTS = "streaming_fragments"
            private const val DOWNLOADING_FRAGMENTS = "downloading_fragments"
            private const val DOWNLOADING_TILES = "downloading_tiles"
            private const val FRAGMENTS_CACHE = "fragments_cache"
            private const val IS_LOADING = "is_loading"
        }
    }

    fun Long.toOdd() = this - this % 2
}