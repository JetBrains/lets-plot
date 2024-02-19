/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.SimpleEventSource
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.div
import org.jetbrains.letsPlot.commons.intern.typedGeometry.plus
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toDoubleVector
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvas.CanvasControlUtil.setAnimationHandler
import org.jetbrains.letsPlot.core.canvas.DeltaTime
import org.jetbrains.letsPlot.livemap.Diagnostics.LiveMapDiagnostics
import org.jetbrains.letsPlot.livemap.api.FeatureLayerBuilder
import org.jetbrains.letsPlot.livemap.chart.*
import org.jetbrains.letsPlot.livemap.chart.fragment.*
import org.jetbrains.letsPlot.livemap.config.DevParams
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.COMPUTATION_FRAME_TIME
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.DEBUG_GRID
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.FRAGMENT_ACTIVE_DOWNLOADS_LIMIT
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.FRAGMENT_CACHE_LIMIT
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.MICRO_TASK_EXECUTOR
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.PERF_STATS
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.RENDER_TARGET
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.SHOW_RESET_POSITION_ACTION
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.TILE_CACHE_LIMIT
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.UPDATE_PAUSE_MS
import org.jetbrains.letsPlot.livemap.config.DevParams.Companion.UPDATE_TIME_MULTIPLIER
import org.jetbrains.letsPlot.livemap.config.DevParams.MicroTaskExecutor.*
import org.jetbrains.letsPlot.livemap.core.MapRuler
import org.jetbrains.letsPlot.livemap.core.ecs.*
import org.jetbrains.letsPlot.livemap.core.graphics.Rectangle
import org.jetbrains.letsPlot.livemap.core.graphics.TextMeasurer
import org.jetbrains.letsPlot.livemap.core.input.*
import org.jetbrains.letsPlot.livemap.core.layers.*
import org.jetbrains.letsPlot.livemap.core.layers.RenderTarget.OWN_OFFSCREEN_CANVAS
import org.jetbrains.letsPlot.livemap.core.layers.RenderTarget.OWN_SCREEN_CANVAS
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskCooperativeExecutor
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskMultiThreadedExecutorFactory
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.jetbrains.letsPlot.livemap.geocoding.ApplyPointSystem
import org.jetbrains.letsPlot.livemap.geocoding.LocationCalculateSystem
import org.jetbrains.letsPlot.livemap.geocoding.LocationCounterSystem
import org.jetbrains.letsPlot.livemap.geocoding.MapLocationInitializationSystem
import org.jetbrains.letsPlot.livemap.makegeometrywidget.MakeGeometryWidgetSystem
import org.jetbrains.letsPlot.livemap.mapengine.*
import org.jetbrains.letsPlot.livemap.mapengine.basemap.*
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug.DebugDataSystem
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraComponent
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraInputSystem
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraScale
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent
import org.jetbrains.letsPlot.livemap.mapengine.camera.MutableCamera
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOrigin2ScreenUpdateSystem
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridUpdateSystem
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportPositionUpdateSystem
import org.jetbrains.letsPlot.livemap.ui.*

class LiveMap(
    private val myMapRuler: MapRuler<World>,
    private val myMapProjection: MapProjection,
    private val viewport: Viewport,
    private val layers: List<FeatureLayerBuilder.() -> Unit>,
    private val myBasemapTileSystemProvider: BasemapTileSystemProvider,
    private val myFragmentProvider: FragmentProvider,
    private val myDevParams: DevParams,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit,
    private val myMapLocationRect: Async<Rect<World>>?,
    private val myZoom: Int?,
    private val myAttribution: String?,
    private val myShowCoordPickTools: Boolean,
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
    private lateinit var myTextMeasurer: TextMeasurer

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

        myLayerManager = when (myRenderTarget) {
            OWN_OFFSCREEN_CANVAS -> OffscreenLayerManager(canvasControl)
            OWN_SCREEN_CANVAS -> ScreenLayerManager(canvasControl)
        }

        myContext = LiveMapContext(
            mapProjection = myMapProjection,
            mouseEventSource = canvasControl,
            mapRenderContext = MapRenderContext(viewport, canvasControl),
            errorHandler = { canvasControl.schedule { errorEvent.fire(it) } },
            camera = camera,
            layerManager = myLayerManager
        )
        myTextMeasurer = TextMeasurer(myContext.mapRenderContext.canvasProvider.createCanvas(Vector.ZERO).context2d)
        myUiService = UiService(myComponentManager, myTextMeasurer)

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

    fun hoverObjects(): List<HoverObject> {
        if (!myInitialized) {
            return emptyList()
        }

        return myComponentManager.getSingleton<SearchResultComponent>().hoverObjects
    }

    private fun animationHandler(componentManager: EcsComponentManager, dt: Long): Boolean {
        if (!myInitialized) {
            init(componentManager)
            myInitialized = true
        }

        myEcsController.update(dt.toDouble())

        myDiagnostics.update(dt)

        return myLayerRenderingSystem.updated
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
                componentManager,
                myLayerManager
            )
        } else {
            Diagnostics()
        }
    }

    private fun initSystems(componentManager: EcsComponentManager) {
        val microTaskExecutor = when (myDevParams.read(MICRO_TASK_EXECUTOR)) {
            UI_THREAD -> MicroTaskCooperativeExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())
            AUTO, BACKGROUND -> MicroTaskMultiThreadedExecutorFactory.create()
        } ?: MicroTaskCooperativeExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())

        myLayerRenderingSystem = LayersRenderingSystem(componentManager, myLayerManager)
        mySchedulerSystem = SchedulerSystem(microTaskExecutor, componentManager)
        myEcsController = EcsController(
            componentManager,
            myContext,
            listOf(
                // Input systems
                MouseInputSystem(componentManager),
                MouseInputDetectionSystem(componentManager, myLayerManager),
                CameraInputSystem(componentManager),
                CursorStyleSystem(componentManager, myCursorService),

                MakeGeometryWidgetSystem(componentManager, myMapProjection, viewport),

                LocationCounterSystem(componentManager, myMapLocationRect == null),
                LocationCalculateSystem(myMapRuler, myMapProjection, componentManager),
                MapLocationInitializationSystem(componentManager, myZoom?.toDouble(), myMapLocationRect),

                ApplyPointSystem(componentManager),

                // Service systems
                AnimationObjectSystem(componentManager),
                AnimationSystem(componentManager),
                ViewportPositionUpdateSystem(componentManager),
                ViewportGridUpdateSystem(componentManager),
                LiveMapUiSystem(
                    myUiService,
                    ResourceManager(myContext.mapRenderContext.canvasProvider),
                    componentManager,
                    myMapLocationConsumer,
                    myLayerManager,
                    myAttribution,
                    myShowCoordPickTools,
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
                WorldOrigin2ScreenUpdateSystem(componentManager),
                HoverObjectDetectionSystem(myUiService, componentManager),

                // Charts
                ChartElementScalingSystem(componentManager),
                MapEntitiesRenderingSystem(componentManager),
                UiEntitiesRenderingSystem(componentManager),

                myLayerRenderingSystem,
                mySchedulerSystem,

                // Effects
                GrowingPathEffect.GrowingPathEffectSystem(componentManager),
                CameraScale.CameraScaleEffectSystem(componentManager)
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
                        origin = org.jetbrains.letsPlot.livemap.Client.ZERO_VEC.toDoubleVector()
                        dimension = viewport.size.toDoubleVector()
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
        if (myBasemapTileSystemProvider.isVector) {
            componentManager
                .createEntity("vector_layer_ground")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.WORLD)
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("ground", LayerKind.BASEMAP_TILES)
                }
        } else {
            componentManager
                .createEntity("raster_layer_ground")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.RASTER)
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("http_ground", LayerKind.BASEMAP_TILES)
                }
        }

        val featureLayerBuilder = FeatureLayerBuilder(
            componentManager,
            myLayerManager,
            myMapProjection,
            myTextMeasurer
        )

        layers.forEach(featureLayerBuilder::apply)

        if (myBasemapTileSystemProvider.isVector) {
            componentManager
                .createEntity("vector_layer_labels")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.LABEL)
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("labels", LayerKind.BASEMAP_LABELS)
                }
        }

        if (myDevParams.isSet(DEBUG_GRID)) {
            componentManager
                .createEntity("cell_layer_debug")
                .addComponents {
                    + BasemapLayerComponent(BasemapLayerKind.DEBUG)
                    + DebugCellLayerComponent()
                    + LayerEntitiesComponent()
                    + myLayerManager.addLayer("debug", LayerKind.BASEMAP_LABELS)
                }
        }

        componentManager
            .createEntity("layer_ui")
            .addComponents {
                + UiEntitiesRenderingSystem.UiLayerComponent()
                + myLayerManager.addLayer("ui", LayerKind.UI)
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

        fun onTime(millisTime: Long): Boolean {
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
