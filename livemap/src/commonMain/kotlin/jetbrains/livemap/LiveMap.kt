/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.div
import jetbrains.datalore.base.projectionGeometry.plus
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.CanvasControlUtil.setAnimationHandler
import jetbrains.datalore.vis.canvas.DeltaTime
import jetbrains.livemap.DevParams.Companion.COMPUTATION_FRAME_TIME
import jetbrains.livemap.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.DevParams.Companion.DEBUG_GRID
import jetbrains.livemap.DevParams.Companion.FRAGMENT_ACTIVE_DOWNLOADS_LIMIT
import jetbrains.livemap.DevParams.Companion.FRAGMENT_CACHE_LIMIT
import jetbrains.livemap.DevParams.Companion.MICRO_TASK_EXECUTOR
import jetbrains.livemap.DevParams.Companion.PERF_STATS
import jetbrains.livemap.DevParams.Companion.RASTER_TILES
import jetbrains.livemap.DevParams.Companion.RENDER_TARGET
import jetbrains.livemap.DevParams.Companion.TILE_CACHE_LIMIT
import jetbrains.livemap.DevParams.Companion.UPDATE_PAUSE_MS
import jetbrains.livemap.DevParams.Companion.UPDATE_TIME_MULTIPLIER
import jetbrains.livemap.DevParams.MicroTaskExecutor.*
import jetbrains.livemap.Diagnostics.LiveMapDiagnostics
import jetbrains.livemap.MapWidgetUtil.MAX_ZOOM
import jetbrains.livemap.camera.*
import jetbrains.livemap.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.input.*
import jetbrains.livemap.core.multitasking.AsyncMicroTaskExecutorFactory
import jetbrains.livemap.core.multitasking.MicroTaskExecutor
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.core.multitasking.SyncMicroTaskExecutor
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.core.rendering.layers.LayerManagers.createLayerManager
import jetbrains.livemap.core.rendering.layers.LayersRenderingSystem
import jetbrains.livemap.core.rendering.layers.RenderTarget
import jetbrains.livemap.core.rendering.primitives.Rectangle
import jetbrains.livemap.effects.GrowingPath
import jetbrains.livemap.entities.geometry.WorldGeometry2ScreenUpdateSystem
import jetbrains.livemap.entities.placement.ScreenLoopsUpdateSystem
import jetbrains.livemap.entities.placement.WorldDimension2ScreenUpdateSystem
import jetbrains.livemap.entities.placement.WorldOrigin2ScreenUpdateSystem
import jetbrains.livemap.entities.regions.*
import jetbrains.livemap.entities.rendering.EntitiesRenderingTaskSystem
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.scaling.ScaleUpdateSystem
import jetbrains.livemap.fragments.FragmentProvider
import jetbrains.livemap.projections.Coordinates
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.newDoubleRectangle
import jetbrains.livemap.projections.toClientPoint
import jetbrains.livemap.tiles.CellStateUpdateSystem
import jetbrains.livemap.tiles.TileLoadingSystemFactory
import jetbrains.livemap.tiles.TileRemovingSystem
import jetbrains.livemap.tiles.TileRequestSystem
import jetbrains.livemap.tiles.components.CellLayerComponent
import jetbrains.livemap.tiles.components.CellLayerKind
import jetbrains.livemap.tiles.components.DebugCellLayerComponent
import jetbrains.livemap.tiles.debug.DebugDataSystem
import jetbrains.livemap.tiles.raster.RasterTileLayerComponent
import jetbrains.livemap.ui.LiveMapUiSystem
import jetbrains.livemap.ui.ResourceManager
import jetbrains.livemap.ui.UiRenderingTaskSystem
import jetbrains.livemap.ui.UiService

class LiveMap(
    private val myMapProjection: MapProjection,
    private val viewport: Viewport,
    private val myLayerProvider: LayerProvider,
    private val myTileLoadingSystemBuilder: TileLoadingSystemFactory,
    private val myFragmentProvider: FragmentProvider,
    private val myDevParams: DevParams,
    private val myEmptinessChecker: EmptinessChecker,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit
) : BaseLiveMap() {
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

    override fun draw(canvasControl: CanvasControl) {
        val componentManager = EcsComponentManager()
        myContext = LiveMapContext(
            myMapProjection,
            canvasControl,
            MapRenderContext(viewport, canvasControl)
        )

        myUiService = UiService(componentManager, ResourceManager(myContext.mapRenderContext.canvasProvider))

        myLayerManager = createLayerManager(componentManager, myRenderTarget, canvasControl)

        val updateController = UpdateController(
            { dt -> animationHandler(componentManager, dt) },
            myDevParams.read(UPDATE_PAUSE_MS).toLong(),
            myDevParams.read(UPDATE_TIME_MULTIPLIER)
        )

        myTimerReg = setAnimationHandler(
            canvasControl,
            AnimationEventHandler.toHandler(updateController::onTime)
        )
    }

    private fun animationHandler(componentManager: EcsComponentManager, dt: Long): Boolean {
        if (!myInitialized) {
            init(componentManager)
            myInitialized = true
        }

        myEcsController.update(dt.toDouble())

        myDiagnostics.update(dt)

        return true
    }

    private fun init(componentManager: EcsComponentManager) {
        initLayers(componentManager)
        initSystems(componentManager)
        initCamera(componentManager)
        myDiagnostics = if (myDevParams.isSet(PERF_STATS)) {
            LiveMapDiagnostics(
                isLoading,
                myLayerRenderingSystem,
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
            UI_THREAD -> SyncMicroTaskExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())
            AUTO, BACKGROUND -> AsyncMicroTaskExecutorFactory.create()
        } ?: SyncMicroTaskExecutor(myContext, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())

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
                CameraUpdateDetectionSystem(componentManager),

                ScaleUpdateSystem(componentManager),

                // Service systems
                AnimationObjectSystem(componentManager),
                AnimationSystem(componentManager),
                ViewProjectionUpdateSystem(componentManager),
                LiveMapUiSystem(myUiService, componentManager, myMapLocationConsumer),

                CellStateUpdateSystem(componentManager),
                TileRequestSystem(componentManager),
                myTileLoadingSystemBuilder.build(componentManager),

                TileRemovingSystem(myDevParams.read(TILE_CACHE_LIMIT), componentManager),
                DebugDataSystem(componentManager),

                //Regions
                FragmentUpdateSystem(componentManager, myEmptinessChecker),
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

                // Geoms
                EntitiesRenderingTaskSystem(componentManager),

                UiRenderingTaskSystem(componentManager),
                myLayerRenderingSystem,
                mySchedulerSystem,

                // Effects
                GrowingPath.GrowingPathEffectSystem(componentManager),
                CameraScale.CameraScaleEffectSystem(componentManager)

                // Tooltips
                //TooltipTargetSystem(componentManager, myRegionGeometryConsumer),

                //LoadingStateSystem(componentManager, isLoading())
            )
        )
    }

    private fun initCamera(componentManager: EcsComponentManager) {
        // Camera
        val listeners = EventListenerComponent()

        val camera = componentManager.getSingletonEntity(CameraComponent::class)
            .addComponents {
                + MouseInputComponent()
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
            if (camera.contains<CameraScaleEffectComponent>() || camera.getComponent<CameraComponent>().zoom == MAX_ZOOM.toDouble()) {
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

        if (myDevParams.isNotSet(RASTER_TILES)) {
            componentManager
                .createEntity("vector_layer_ground")
                .addComponents {
                    + CellLayerComponent(CellLayerKind.WORLD)
                    + LayerEntitiesComponent()
                    + myLayerManager.createRenderLayerComponent("ground", LayerGroup.BACKGROUND)
                }
        } else {
            componentManager
                .createEntity("raster_layer_ground")
                .addComponents {
                    + CellLayerComponent(CellLayerKind.RASTER)
                    + RasterTileLayerComponent()
                    + LayerEntitiesComponent()
                    + myLayerManager.createRenderLayerComponent("http_ground", LayerGroup.BACKGROUND)
                }
        }

        myLayerProvider.provide(
            componentManager,
            myLayerManager,
            myMapProjection,
            myContext.mapRenderContext.canvasProvider.createCanvas(Vector.ZERO).context2d
        )

        if (myDevParams.isNotSet(RASTER_TILES)) {
            componentManager
                .createEntity("vector_layer_labels")
                .addComponents {
                    + CellLayerComponent(CellLayerKind.LABEL)
                    + LayerEntitiesComponent()
                    + myLayerManager.createRenderLayerComponent("labels", LayerGroup.FOREGROUND)
                }
        }

        if (myDevParams.isSet(DEBUG_GRID)) {
            componentManager
                .createEntity("cell_layer_debug")
                .addComponents {
                    + CellLayerComponent(CellLayerKind.DEBUG)
                    + DebugCellLayerComponent()
                    + LayerEntitiesComponent()
                    + myLayerManager.createRenderLayerComponent("debug", LayerGroup.FOREGROUND)
                }
        }

        componentManager
            .createEntity("layer_ui")
            .addComponents {
                + UiRenderingTaskSystem.UiLayerComponent()
                + myLayerManager.createRenderLayerComponent("ui", LayerGroup.UI)
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