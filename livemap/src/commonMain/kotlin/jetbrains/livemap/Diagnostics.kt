package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.MetricsService
import jetbrains.livemap.core.Utils.formatDouble
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.core.rendering.layers.LayersRenderingSystem
import jetbrains.livemap.core.rendering.layers.RenderLayer
import jetbrains.livemap.core.rendering.layers.RenderLayerComponent
import jetbrains.livemap.core.rendering.primitives.Label
import jetbrains.livemap.core.rendering.primitives.Text
import jetbrains.livemap.entities.regions.CachedFragmentsComponent
import jetbrains.livemap.entities.regions.DownloadingFragmentsComponent
import jetbrains.livemap.entities.regions.StreamingFragmentsComponent
import jetbrains.livemap.ui.UiService

open class Diagnostics {

    open fun update(dt: Long) {}

    class LiveMapDiagnostics(
        isLoading: Property<Boolean>,
        private val layersOrder: List<RenderLayer>,
        private val layerRenderingSystem: LayersRenderingSystem,
        private val schedulerSystem: SchedulerSystem,
        private val debugService: MetricsService,
        uiService: UiService,
        private val componentManager: EcsComponentManager
    ) : Diagnostics() {

        private val diagnostics = ArrayList<Diagnostic>()

        private var deltaTime: Long = 0
        private val metrics: Text

        private val slowestSystemType: String?
            get() = debugService.slowestSystem()?.first!!::class.toString()

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
                    IS_LOADING
                )
            )

            metrics = Text().apply {
                color = Color.DARK_GREEN
                fontFamily = "Courier New"
                fontHeight = 12.0
            }

            val metricsLabel = Label(DoubleVector(0.0, 150.0), metrics).apply {
                padding = 4.0
                background = Color.WHITE
            }

            uiService.addRenderable(metricsLabel)
        }

        override fun update(dt: Long) {
            deltaTime = dt
            debugService.setValue(TIMER_TICK, "Timer tick: $deltaTime")
            debugService.setValue(
                SYSTEMS_UPDATE_TIME,
                "Systems update: ${formatDouble(debugService.totalUpdateTime, 1)}"
            )
            debugService.setValue(ENTITIES_COUNT, "Entities count: ${componentManager.entitiesCount}")

            diagnostics.forEach { it.update() }

            metrics.text = debugService.values
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
                val dirtyLayers = ArrayList<RenderLayer>()
                for (dirtyLayerEntity in componentManager.getEntitiesById(layerRenderingSystem.dirtyLayers)) {
                    dirtyLayers.add(dirtyLayerEntity.get<RenderLayerComponent>().renderLayer)
                }

                val dirtyLayersString = StringBuilder()
                for (renderLayer in layersOrder) {
                    if (dirtyLayers.contains(renderLayer)) {
                        if (dirtyLayersString.isNotEmpty()) {
                            dirtyLayersString.append(", ")
                        }
                        dirtyLayersString.append(renderLayer.name)
                    }
                }

                debugService.setValue(DIRTY_LAYERS, "Dirty layers: $dirtyLayersString")
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
                val tasksCount = componentManager.getComponentsCount(MicroThreadComponent::class)
                debugService.setValue(SCHEDULER_SYSTEM, "Micro threads: $tasksCount, ${schedulerSystem.loading}")
            }
        }

        internal inner class FragmentsCacheDiagnostic : Diagnostic {

            override fun update() {
                val size =
                    if (componentManager.containsSingletonEntity(CachedFragmentsComponent::class))
                        componentManager.getSingletonComponent<CachedFragmentsComponent>().keys().size
                    else
                        0

                debugService.setValue(FRAGMENTS_CACHE, "Fragments cache: $size")
            }
        }

        internal inner class StreamingFragmentsDiagnostic : Diagnostic {

            override fun update() {
                val size =
                    if (componentManager.containsSingletonEntity(StreamingFragmentsComponent::class))
                        componentManager.getSingletonComponent<StreamingFragmentsComponent>().keys().size
                    else
                        0
                debugService.setValue(STREAMING_FRAGMENTS, "Streaming fragments: $size")
            }
        }

        internal inner class DownloadingFragmentsDiagnostic : Diagnostic {

            override fun update() {
                val downloading: Int
                val queued: Int

                if (componentManager.containsSingletonEntity(DownloadingFragmentsComponent::class)) {
                    componentManager.getSingletonComponent<DownloadingFragmentsComponent>().let {
                        downloading = it.downloading.size
                        queued = it.queue.values.sumBy { queue -> queue.size }
                    }

                } else {
                    downloading = 0
                    queued = 0
                }

                debugService.setValue(DOWNLOADING_FRAGMENTS, "Downloading fragments: D: $downloading Q: $queued")
            }
        }

        internal inner class IsLoadingDiagnostic(private val isLoading: Property<Boolean>) : Diagnostic {

            override fun update() {
                debugService.setValue(IS_LOADING, "Is loading: ${isLoading.get()}")
            }
        }

        internal interface Diagnostic {
            fun update()
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
            private const val FRAGMENTS_CACHE = "fragments_cache"
            private const val IS_LOADING = "is_loading"
        }
    }
}