/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.SimpleEventSource
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.div
import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.CanvasControlUtil.setAnimationHandler
import jetbrains.datalore.vis.canvas.DeltaTime
import jetbrains.livemap.Diagnostics.LiveMapDiagnostics
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.chart.ChartElementScaleSystem
import jetbrains.livemap.chart.GrowingPathEffect
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.DevParams.Companion.COMPUTATION_FRAME_TIME
import jetbrains.livemap.config.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.config.DevParams.Companion.DEBUG_GRID
import jetbrains.livemap.config.DevParams.Companion.FRAGMENT_ACTIVE_DOWNLOADS_LIMIT
import jetbrains.livemap.config.DevParams.Companion.FRAGMENT_CACHE_LIMIT
import jetbrains.livemap.config.DevParams.Companion.MICRO_TASK_EXECUTOR
import jetbrains.livemap.config.DevParams.Companion.PERF_STATS
import jetbrains.livemap.config.DevParams.Companion.RENDER_TARGET
import jetbrains.livemap.config.DevParams.Companion.SCALABLE_SYMBOLS_ZOOM_IN_MULTIPLIER
import jetbrains.livemap.config.DevParams.Companion.SCALABLE_SYMBOLS_ZOOM_OUT_MULTIPLIER
import jetbrains.livemap.config.DevParams.Companion.SHOW_ADVANCED_ACTIONS
import jetbrains.livemap.config.DevParams.Companion.SHOW_RESET_POSITION_ACTION
import jetbrains.livemap.config.DevParams.Companion.TILE_CACHE_LIMIT
import jetbrains.livemap.config.DevParams.Companion.UPDATE_PAUSE_MS
import jetbrains.livemap.config.DevParams.Companion.UPDATE_TIME_MULTIPLIER
import jetbrains.livemap.config.DevParams.MicroTaskExecutor.*
import jetbrains.livemap.core.BusyStateSystem
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.input.*
import jetbrains.livemap.core.multitasking.MicroTaskCooperativeExecutor
import jetbrains.livemap.core.multitasking.MicroTaskExecutor
import jetbrains.livemap.core.multitasking.MicroTaskMultiThreadedExecutorFactory
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.core.rendering.TextMeasurer
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.core.rendering.layers.LayerManagers.createLayerManager
import jetbrains.livemap.core.rendering.layers.LayersRenderingSystem
import jetbrains.livemap.core.rendering.layers.RenderTarget
import jetbrains.livemap.core.rendering.primitives.Rectangle
import jetbrains.livemap.fragment.*
import jetbrains.livemap.geocoding.ApplyPointSystem
import jetbrains.livemap.geocoding.LocationCalculateSystem
import jetbrains.livemap.geocoding.LocationCounterSystem
import jetbrains.livemap.geocoding.MapLocationInitializationSystem
import jetbrains.livemap.geometry.ScaleUpdateSystem
import jetbrains.livemap.geometry.WorldGeometry2ScreenUpdateSystem
import jetbrains.livemap.makegeometrywidget.MakeGeometryWidgetSystem
import jetbrains.livemap.mapengine.*
import jetbrains.livemap.mapengine.basemap.*
import jetbrains.livemap.mapengine.basemap.raster.RasterTileLayerComponent
import jetbrains.livemap.mapengine.basemap.vector.debug.DebugDataSystem
import jetbrains.livemap.mapengine.camera.CameraComponent
import jetbrains.livemap.mapengine.camera.CameraInputSystem
import jetbrains.livemap.mapengine.camera.CameraScale
import jetbrains.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.mapengine.camera.MutableCamera
import jetbrains.livemap.mapengine.placement.ScreenLoopsUpdateSystem
import jetbrains.livemap.mapengine.placement.WorldDimension2ScreenUpdateSystem
import jetbrains.livemap.mapengine.placement.WorldOrigin2ScreenUpdateSystem
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportGridUpdateSystem
import jetbrains.livemap.mapengine.viewport.ViewportPositionUpdateSystem
import jetbrains.livemap.searching.HoverObjectComponent
import jetbrains.livemap.searching.HoverObjectDetectionSystem
import jetbrains.livemap.searching.SearchResult
import jetbrains.livemap.ui.*

class LiveMap(
    private val myMapRuler: MapRuler<World>,
    private val myMapProjection: MapProjection,
    private val viewport: Viewport,
    private val layers: List<LayersBuilder.() -> Unit>,
    private val myBasemapTileSystemProvider: BasemapTileSystemProvider,
    private val myFragmentProvider: FragmentProvider,
    private val myDevParams: DevParams,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit,
    private val myMapLocationRect: Async<Rect<World>>?,
    private val myZoom: Int?,
    private val myAttribution: String?,
    private val myCursorService: CursorService
) : Disposable {
    private val myRenderTarget: RenderTarget = myDevParams.read(RENDER_TARGET)
    private var myTimerReg = Registration.EMPTY
    private var myInitialized: Boolean = false
    private lateinit var myEcsController: EcsController
    private lateinit var myContext: LiveMapContext
    private lateinit var myLayerRenderingSystem: LayersRenderingSystem
    private lateinit var myLayerManager: LayerManager
    private lateinit var myDiagnostics: Diagnostics
    private lateinit var mySchedulerSystem: SchedulerSystem
    private lateinit var myUiService: UiService

    private val errorEvent = SimpleEventSource<Throwable>()
    val isLoading: Property<Boolean> = ValueProperty(true)

    fun addErrorHandler(handler: (Throwable) -> Unit): Registration {
        return errorEvent.addHandler(
            object : EventHandler<Throwable> {
                override fun onEvent(event: Throwable) = handler(event)
            }
        )
    }

    private val myComponentManager = EcsComponentManager()

    fun draw(canvasControl: CanvasControl) {
        val camera = MutableCamera(myComponentManager)
            .apply {
                requestZoom(viewport.zoom.toDouble())
                requestPosition(viewport.position)
            }

        myContext = LiveMapContext(
            mapProjection = myMapProjection,
            mouseEventSource = canvasControl,
            mapRenderContext = MapRenderContext(viewport, canvasControl),
            errorHandler = { canvasControl.schedule { errorEvent.fire(it) } },
            camera = camera
        )

        myUiService = UiService(myComponentManager, ResourceManager(myContext.mapRenderContext.canvasProvider))

        myLayerManager = createLayerManager(myComponentManager, myRenderTarget, canvasControl)

        val updateController = UpdateController(
            { dt -> animationHandler(myComponentManager, dt) },
            myDevParams.read(UPDATE_PAUSE_MS).toLong(),
            myDevParams.read(UPDATE_TIME_MULTIPLIER)
        )

        myTimerReg = setAnimationHandler(
            canvasControl,
            AnimationEventHandler.toHandler(updateController::onTime)
        )
    }

    fun searchResult(): SearchResult? {
        if (!myInitialized) {
            return null
        }

        return myComponentManager.getSingleton<HoverObjectComponent>().searchResult
    }

    private fun animationHandler(componentManager: EcsComponentManager, dt: Long): Boolean {
        if (!myInitialized) {
            init(componentManager)
            myInitialized = true
        }

        myEcsController.update(dt.toDouble())

        myDiagnostics.update(dt)

        return myLayerRenderingSystem.dirtyLayers.isNotEmpty()
    }

    private fun init(componentManager: EcsComponentManager) {
        initLayers(componentManager)
        initSystems(componentManager)
        initCamera(componentManager)
        myDiagnostics = if (myDevParams.isSet(PERF_STATS)) {
            LiveMapDiagnostics(
                isLoading,
                myLayerRenderingSystem.dirtyLayers,
                mySchedulerSystem,
                myContext.metricsService,
                myUiService,
                componentManager
            )
        } else {
            Diagnostics()
        }
    }

    private fun initSystems(componentManager: EcsComponentManager) {
        val microTaskExecutor: MicroTaskExecutor = when (myDevParams.read(MICRO_TASK_EXECUTOR)) {
            UI_THREAD -> MicroTaskCooperativeExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())
            AUTO, BACKGROUND -> MicroTaskMultiThreadedExecutorFactory.create()
        } ?: MicroTaskCooperativeExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())

        myLayerRenderingSystem = myLayerManager.createLayerRenderingSystem()
        mySchedulerSystem = SchedulerSystem(microTaskExecutor, componentManager)
        myEcsController = EcsController(
            componentManager,
            myContext,
            listOf(
                // Input systems
                MouseInputSystem(componentManager),
                MouseInputDetectionSystem(componentManager),
                CameraInputSystem(componentManager),
                CursorStyleSystem(componentManager, myCursorService),

                MakeGeometryWidgetSystem(componentManager, myMapProjection, viewport),

                LocationCounterSystem(componentManager, myMapLocationRect == null),
                LocationCalculateSystem(myMapRuler, myMapProjection, componentManager),
                MapLocationInitializationSystem(componentManager, myZoom?.toDouble(), myMapLocationRect),

                ApplyPointSystem(componentManager),

                ScaleUpdateSystem(componentManager),

                // Service systems
                AnimationObjectSystem(componentManager),
                AnimationSystem(componentManager),
                ViewportPositionUpdateSystem(componentManager),
                ViewportGridUpdateSystem(componentManager),
                LiveMapUiSystem(
                    myUiService,
                    componentManager,
                    myMapLocationConsumer,
                    myLayerManager,
                    myAttribution,
                    myDevParams.isSet(SHOW_ADVANCED_ACTIONS),
                    myDevParams.isSet(SHOW_RESET_POSITION_ACTION),
                ),

                BasemapCellLoadingSystem(componentManager),
                myBasemapTileSystemProvider.create(componentManager),

                BasemapCellsRemovingSystem(myDevParams.read(TILE_CACHE_LIMIT), componentManager),
                DebugDataSystem(componentManager),

                //Regions
                FragmentUpdateSystem(componentManager),
                FragmentDownloadingSystem(
                    myDevParams.read(FRAGMENT_ACTIVE_DOWNLOADS_LIMIT),
                    myFragmentProvider,
                    componentManager
                ),
                FragmentEmitSystem(myDevParams.read(COMPUTATION_PROJECTION_QUANT), componentManager),
                RegionEmitSystem(componentManager),
                FragmentsRemovingSystem(myDevParams.read(FRAGMENT_CACHE_LIMIT), componentManager),

                // Position update
                WorldDimension2ScreenUpdateSystem(componentManager),
                WorldOrigin2ScreenUpdateSystem(componentManager),
                WorldGeometry2ScreenUpdateSystem(myDevParams.read(COMPUTATION_PROJECTION_QUANT), componentManager),
                ScreenLoopsUpdateSystem(componentManager),
                HoverObjectDetectionSystem(componentManager),

                // Charts
                ChartElementScaleSystem(
                    zoomInMultiplier = myDevParams.read(SCALABLE_SYMBOLS_ZOOM_IN_MULTIPLIER),
                    zoomOutMultiplier = myDevParams.read(SCALABLE_SYMBOLS_ZOOM_OUT_MULTIPLIER),
                    componentManager
                ),
                RenderingSystem(componentManager),

                BusyStateSystem(componentManager, myUiService),

                UiRenderingTaskSystem(componentManager),
                myLayerRenderingSystem,
                mySchedulerSystem,

                // Effects
                GrowingPathEffect.GrowingPathEffectSystem(componentManager),
                CameraScale.CameraScaleEffectSystem(componentManager)

                //LoadingStateSystem(componentManager, isLoading())
            )
        )
    }

    private fun initCamera(componentManager: EcsComponentManager) {
        // Camera
        val listeners = EventListenerComponent()

        val camera = componentManager.getSingletonEntity<CameraComponent>()
            .addComponents {
                + ClickableComponent(
                    Rectangle().apply {
                        rect = newDoubleRectangle(
                            Coordinates.ZERO_CLIENT_POINT,
                            viewport.size
                        )
                    }
                )
                + listeners
            }

        listeners.addDoubleClickListener { clickEvent ->
            if (camera.contains<CameraScaleEffectComponent>() || camera.getComponent<CameraComponent>().zoom == viewport.maxZoom.toDouble()) {
                return@addDoubleClickListener
            }

            val location = clickEvent.location.toClientPoint()
            val newViewportPosition = viewport.getMapCoord((location + viewport.center) / 2.0)
            CameraScale.setAnimation(camera, location, newViewportPosition, 1.0)
        }
    }

    private fun initLayers(componentManager: EcsComponentManager) {

        componentManager
            .createEntity("layers_order")
            .addComponents { + myLayerManager.createLayersOrderComponent() }

        if (myBasemapTileSystemProvider.isVector) {
            componentManager
                .createEntity("vector_layer_ground")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.WORLD)
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("ground", LayerGroup.BACKGROUND)
                }
        } else {
            componentManager
                .createEntity("raster_layer_ground")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.RASTER)
                    + RasterTileLayerComponent()
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("http_ground", LayerGroup.BACKGROUND)
                }
        }

        val layersBuilder = LayersBuilder(
            componentManager,
            myLayerManager,
            myMapProjection,
            myDevParams.isSet(DevParams.SCALABLE_SYMBOLS),
            TextMeasurer(myContext.mapRenderContext.canvasProvider.createCanvas(Vector.ZERO).context2d)
        )

        layers.forEach(layersBuilder::apply)

        if (myBasemapTileSystemProvider.isVector) {
            componentManager
                .createEntity("vector_layer_labels")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.LABEL)
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("labels", LayerGroup.FOREGROUND)
                }
        }

        if (myDevParams.isSet(DEBUG_GRID)) {
            componentManager
                .createEntity("cell_layer_debug")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.DEBUG)
                    + DebugCellLayerComponent()
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("debug", LayerGroup.FOREGROUND)
                }
        }

        componentManager
            .createEntity("layer_ui")
            .addComponents {
                + UiRenderingTaskSystem.UiLayerComponent()
                + myLayerManager.addLayer("ui", LayerGroup.UI)
            }
    }

    override fun dispose() {
        myTimerReg.dispose()
        myEcsController.dispose()
    }

    private class UpdateController(
        private val timePredicate: (Long) -> Boolean,
        private val skipTime: Long,
        private val animationMultiplier: Double
    ) {
        private val deltaTime = DeltaTime()
        private var currentTime: Long = 0

        internal fun onTime(millisTime: Long): Boolean {
            val dt = deltaTime.tick(millisTime)
            currentTime += dt

            if (currentTime > skipTime) {
                currentTime = 0
                return timePredicate((dt * animationMultiplier).toLong())
            }

            return false
        }
    }
}
