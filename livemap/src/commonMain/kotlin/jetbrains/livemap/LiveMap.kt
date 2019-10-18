package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.div
import jetbrains.datalore.base.projectionGeometry.minus
import jetbrains.datalore.base.projectionGeometry.plus
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.CanvasControlUtil.setAnimationHandler
import jetbrains.datalore.vis.canvas.DeltaTime
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.DevParams.Companion.COMPUTATION_FRAME_TIME
import jetbrains.livemap.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.DevParams.Companion.DEBUG_GRID
import jetbrains.livemap.DevParams.Companion.DEBUG_TILES
import jetbrains.livemap.DevParams.Companion.FRAGMENT_ACTIVE_DOWNLOADS_LIMIT
import jetbrains.livemap.DevParams.Companion.FRAGMENT_CACHE_LIMIT
import jetbrains.livemap.DevParams.Companion.MICRO_TASK_EXECUTOR
import jetbrains.livemap.DevParams.Companion.PERF_STATS
import jetbrains.livemap.DevParams.Companion.RENDER_TARGET
import jetbrains.livemap.DevParams.Companion.TILE_CACHE_LIMIT
import jetbrains.livemap.DevParams.Companion.UPDATE_PAUSE_MS
import jetbrains.livemap.DevParams.Companion.UPDATE_TIME_MULTIPLIER
import jetbrains.livemap.DevParams.MicroTaskExecutor.*
import jetbrains.livemap.Diagnostics.LiveMapDiagnostics
import jetbrains.livemap.MapWidgetUtil.MAX_ZOOM
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraInputSystem
import jetbrains.livemap.camera.CameraScale
import jetbrains.livemap.camera.CameraUpdateDetectionSystem
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.input.*
import jetbrains.livemap.core.multitasking.AsyncMicroTaskExecutorFactory
import jetbrains.livemap.core.multitasking.MicroTaskExecutor
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.core.multitasking.SyncMicroTaskExecutor
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.core.rendering.layers.LayerManagers.createLayerManager
import jetbrains.livemap.core.rendering.layers.LayersRenderingSystem
import jetbrains.livemap.core.rendering.layers.RenderLayer
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
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.obj2entity.MapObject2Entity
import jetbrains.livemap.obj2entity.TextMeasurer
import jetbrains.livemap.projections.*
import jetbrains.livemap.tilegeometry.TileGeometryProvider
import jetbrains.livemap.tiles.*
import jetbrains.livemap.tiles.components.CellLayerComponent
import jetbrains.livemap.tiles.components.CellLayerKind
import jetbrains.livemap.tiles.components.DebugCellLayerComponent
import jetbrains.livemap.tiles.http.HttpTileLoadingSystem
import jetbrains.livemap.ui.LiveMapUiSystem
import jetbrains.livemap.ui.ResourceManager
import jetbrains.livemap.ui.UiRenderingTaskSystem
import jetbrains.livemap.ui.UiService

class LiveMap(
    private val myMapProjection: MapProjection,
    private val viewProjection: ViewProjection,
    private val myMapLayers: List<MapLayer>,
    private val myTileService: TileService,
    private val myTileGeometryProvider: TileGeometryProvider,
    private val myDevParams: DevParams,
    private val myEmptinessChecker: EmptinessChecker,
    private val myMapLocationConsumer: (DoubleRectangle) -> Unit
) : BaseLiveMap() {
    private val renderTarget: RenderTarget = myDevParams.read(RENDER_TARGET)
    private var ecsController: EcsController? = null
    private var myTimerReg = Registration.EMPTY
    private lateinit var context: LiveMapContext
    private lateinit var layerRenderingSystem: LayersRenderingSystem
    private lateinit var layersOrder: List<RenderLayer>
    private var myInitialized: Boolean = false
    private var myLayerManager: LayerManager? = null
    private lateinit var myDiagnostics: Diagnostics
    private lateinit var schedulerSystem: SchedulerSystem
    private lateinit var uiService: UiService

    override fun draw(canvasControl: CanvasControl) {
        val componentManager = EcsComponentManager()
        context = LiveMapContext(
            myMapProjection,
            canvasControl,
            MapRenderContext(viewProjection, canvasControl),
            null
        )

        uiService = UiService(componentManager, ResourceManager(context.mapRenderContext.canvasProvider))

        myLayerManager = createLayerManager(componentManager, renderTarget, canvasControl)

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
            initLayers(myLayerManager!!, componentManager)
            initSystems(componentManager)
            initCamera(componentManager)
            myDiagnostics = if (myDevParams.isSet(PERF_STATS)) {
                LiveMapDiagnostics(
                    isLoading,
                    layersOrder,
                    layerRenderingSystem,
                    schedulerSystem,
                    context.metricsService,
                    uiService,
                    componentManager
                )
            } else {
                Diagnostics()
            }

            myInitialized = true
        }

        ecsController?.update(dt.toDouble())

        myDiagnostics.update(dt)

        return true
    }

    private fun initSystems(componentManager: EcsComponentManager) {
        val tileLoadingSystem =
            if (myDevParams.isSet(DEBUG_TILES))
                object : AbstractSystem<LiveMapContext>(componentManager) {}
            else
                TileLoadingSystem(myDevParams.read(COMPUTATION_PROJECTION_QUANT), myTileService, componentManager)


        val microTaskExecutor: MicroTaskExecutor = when (myDevParams.read(MICRO_TASK_EXECUTOR)) {
            UI_THREAD -> SyncMicroTaskExecutor(context, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())
            AUTO, BACKGROUND -> AsyncMicroTaskExecutorFactory.create()
        } ?: SyncMicroTaskExecutor(context, myDevParams.read(COMPUTATION_FRAME_TIME).toLong())


        schedulerSystem = SchedulerSystem(microTaskExecutor, componentManager)
        ecsController = EcsController(
            componentManager,
            context,
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
                LiveMapUiSystem(uiService, componentManager, myMapLocationConsumer),

                CellStateUpdateSystem(componentManager),
                TileRequestSystem(componentManager),

                tileLoadingSystem,
                HttpTileLoadingSystem(componentManager),

                TileRemovingSystem(myDevParams.read(TILE_CACHE_LIMIT), componentManager),
                DebugDataSystem(componentManager),

                //Regions
                FragmentUpdateSystem(componentManager, myEmptinessChecker),
                FragmentDownloadingSystem(
                    myDevParams.read(FRAGMENT_ACTIVE_DOWNLOADS_LIMIT),
                    myTileGeometryProvider,
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
                layerRenderingSystem,
                schedulerSystem,

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
                            viewProjection.viewSize
                        )
                    }
                )
                + listeners
            }

        listeners.addDoubleClickListener { event ->
            if (camera.contains(CameraScale.CameraScaleEffectComponent::class) || camera.getComponent<CameraComponent>().zoom == MAX_ZOOM.toDouble()) {
                return@addDoubleClickListener
            }

            val origin = event.location!!.let { ClientPoint(it.x, it.y) }
            val currentMapCenter = viewProjection.getMapCoord(viewProjection.viewSize / 2.0)

            CameraScale.setAnimation(
                camera,
                origin,
                viewProjection.getMapCoord(origin)
                    .run { this - currentMapCenter }
                    .run { this / 2.0 }
                    .run { this + currentMapCenter},
                1.0
            )
        }
    }

    private fun initLayers(layerManager: LayerManager, componentManager: EcsComponentManager) {
        // layers
        layersOrder = layerManager.createLayersOrderComponent().renderLayers
        layerRenderingSystem = layerManager.createLayerRenderingSystem()

        componentManager
            .createEntity("layers_order")
            .addComponents { + layerManager.createLayersOrderComponent() }

        componentManager
            .createEntity("http_tile_layer")
            .addComponents {
                + CellLayerComponent(CellLayerKind.HTTP)
                + LayerEntitiesComponent()
                + layerManager.createRenderLayerComponent("http_ground")
            }

        componentManager
            .createEntity("cell_layer_ground")
            .addComponents {
                + CellLayerComponent(CellLayerKind.WORLD)
                + LayerEntitiesComponent()
                + layerManager.createRenderLayerComponent("ground")
            }

        val mapObject2Entity = MapObject2Entity(componentManager, layerManager, myDevParams, myMapProjection)
        for (mapLayer in myMapLayers) {
            val kind = mapLayer.kind
            val mapObjects = mapLayer.mapObjects

            when(kind) {
                MapLayerKind.POINT -> mapObject2Entity.processPoint(mapObjects)
                MapLayerKind.PATH -> mapObject2Entity.processPath(mapObjects)
                MapLayerKind.POLYGON -> mapObject2Entity.processPolygon(mapObjects)
                MapLayerKind.BAR -> mapObject2Entity.processBar(mapObjects)
                MapLayerKind.PIE -> mapObject2Entity.processPie(mapObjects)
                MapLayerKind.H_LINE -> mapObject2Entity.processLine(mapObjects, true)
                MapLayerKind.V_LINE -> mapObject2Entity.processLine(mapObjects, false)
                MapLayerKind.TEXT -> mapObject2Entity.processText(
                    mapObjects,
                    TextMeasurer(context.mapRenderContext.canvasProvider.createCanvas(Vector.ZERO).context2d)
                )
                else -> error("")
            }
        }

        componentManager
            .createEntity("cell_layer_labels")
            .addComponents {
                + CellLayerComponent(CellLayerKind.LABEL)
                + LayerEntitiesComponent()
                + layerManager.createRenderLayerComponent("labels")
            }

        if (myDevParams.isSet(DEBUG_GRID)) {
            componentManager
                .createEntity("cell_layer_debug")
                .addComponents {
                    + CellLayerComponent(CellLayerKind.DEBUG)
                    + DebugCellLayerComponent()
                    + LayerEntitiesComponent()
                    + layerManager.createRenderLayerComponent("debug")
                }
        }

        componentManager
            .createEntity("layer_ui")
            .addComponents {
                + UiRenderingTaskSystem.UiLayerComponent()
                + layerManager.createRenderLayerComponent("ui")
            }
    }

    override fun dispose() {
        myTimerReg.dispose()
        ecsController?.dispose()
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