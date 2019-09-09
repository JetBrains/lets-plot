package jetbrains.livemap.tiles

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.getTileRect
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.plot.builder.layout.GeometryUtil.round
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroTaskUtil
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.Entities.mapEntity
import jetbrains.livemap.entities.placement.Components.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.WorldDimension2ScreenUpdateSystem.Companion.world2Screen
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.CellStateUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.tiles.Tile.SnapshotTile
import jetbrains.livemap.tiles.components.*
import jetbrains.livemap.tiles.components.RendererCacheComponent.Companion.NULL_RENDERER

class TileLoadingSystem(
    private val myQuantumIterations: Int,
    private val myTileService: TileService,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myMapRect: DoubleRectangle
    private lateinit var myCanvasSupplier: () -> Canvas
    private lateinit var myTileDataFetcher: TileDataFetcher
    private lateinit var myTileDataParser: TileDataParser
    private lateinit var myTileDataRenderer: TileDataRenderer
    private lateinit var myDonorTileCalculators: Map<CellLayerKind, DonorTileCalculator>

    override fun initImpl(context: LiveMapContext) {
        myMapRect = context.mapProjection.mapRect
        val dimension = round(myMapRect.dimension)
        myCanvasSupplier = { context.mapRenderContext.canvasProvider.createCanvas(dimension) }

        myTileDataFetcher = TileDataFetcherImpl(context.mapProjection, myTileService)
        myTileDataParser = TileDataParserImpl(context.mapProjection)
        myTileDataRenderer = TileDataRendererImpl(myTileService::mapConfig)

        run {
            // enable debug stats
            val stats: StatisticsComponent = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS).get()
            myTileDataFetcher = DebugTileDataFetcher(stats, context.systemTime, myTileDataFetcher)
            myTileDataParser = DebugTileDataParser(stats, context.systemTime, myTileDataParser)
            myTileDataRenderer = DebugTileDataRenderer(stats, context.systemTime, myTileDataRenderer)
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myDonorTileCalculators = createDonorTileCalculators()

        val requestTiles = HashSet(
            getSingletonEntity(CellStateComponent::class)
                .getComponent<CellStateComponent>()
                .requestCells
        )

        getEntities(CellComponent::class).forEach { cellEntity ->
            requestTiles.remove(
                CellComponent.getCellKey(cellEntity)
            )
        }

        requestTiles.forEach { cellKey ->
            val tileResponseComponent = TileResponseComponent()

            createEntity("tile_$cellKey")
                .addComponent(CellComponent(cellKey))
                .addComponent(tileResponseComponent)

            createTileLayerEntities(cellKey)

            myTileDataFetcher.fetch(cellKey).onResult(
                { tileResponseComponent.tileData = it },
                { tileResponseComponent.tileData = emptyList() }
            )
        }

        val downloadedEntities = ArrayList<EcsEntity>()
        for (entity in getEntities(TileResponseComponent::class)) {
            val tileData = TileResponseComponent.getTileData(entity) ?: continue
            downloadedEntities.add(entity)

            val cellKey = CellComponent.getCellKey(entity)
            val tileLayerEntities = getTileLayerEntities(cellKey)

            val microThreadComponent = MicroThreadComponent(
                myTileDataParser
                    .parse(cellKey, tileData)
                    .flatMap { tileFeatures ->
                        val microThreads = ArrayList<MicroTask<Unit>>()
                        tileLayerEntities.forEach { tileLayerEntity ->
                            microThreads.add(
                                myTileDataRenderer
                                    .render(myCanvasSupplier(), tileFeatures, cellKey, KindComponent.getLayerKind(tileLayerEntity))
                                    .map { snapshotAsync ->
                                        snapshotAsync.onSuccess { snapshot ->
                                            runLaterBySystem(tileLayerEntity) { theEntity ->
                                                TileComponent.setTile(theEntity, SnapshotTile(snapshot))
                                                ParentLayerComponent.tagDirtyParentLayer(theEntity)
                                            }
                                        }
                                        return@map
                                    }
                            )
                        }
                        MicroTaskUtil.join(microThreads)
                    },
                myQuantumIterations
            )

            entity.addComponent(microThreadComponent)
        }

        downloadedEntities.forEach { entity -> entity.removeComponent(TileResponseComponent::class) }
    }

    private fun createTileLayerEntities(cellKey: CellKey) {
        val zoom = cellKey.length

        val cellMapRect = getTileRect(myMapRect, cellKey.toString())

        for (layer in getEntities(CellLayerComponent::class)) {
            val layerEntities = layer.getComponent<LayerEntitiesComponent>()
            val layerKind = CellLayerComponent.getKind(layer)

            val donorTile = calculateDonorTile(layerKind, cellKey)

            val parentLayerComponent = ParentLayerComponent(layer.id)
            val name = "tile_${layerKind}_$cellKey"
            val tileLayerEntity =
                mapEntity(componentManager, cellMapRect.origin, parentLayerComponent, NULL_RENDERER, name)
                    .addComponent(ScreenDimensionComponent().apply {
                        dimension = world2Screen(cellMapRect.dimension, zoom.toDouble())
                    })
                    .addComponent(CellComponent(cellKey))
                    .addComponent(KindComponent(layerKind))
                    .addComponent(RendererCacheComponent().apply {
                        renderer = getRenderer(layer)
                    })

            when {
                layer.contains(DebugCellLayerComponent::class) ->
                    tileLayerEntity.addComponent(DebugDataComponent())
                else ->
                    tileLayerEntity.addComponent(TileComponent().setTile(donorTile))
            }

            layerEntities.add(tileLayerEntity.id)
        }
    }

    private fun getTileLayerEntities(cellKey: CellKey): Iterable<EcsEntity> {
        return getEntities(CELL_COMPONENT_LIST)
            .filter { CellComponent.getCellKey(it) == cellKey && KindComponent.getLayerKind(it) != CellLayerKind.DEBUG }
    }

    private fun getRenderer(layer: EcsEntity): Renderer = when {
        layer.contains(DebugCellLayerComponent::class) -> DebugCellRenderer()
        else -> TileRenderer()
    }

    private fun calculateDonorTile(layerKind: CellLayerKind, cellKey: CellKey): Tile? {
        return myDonorTileCalculators[layerKind]?.createDonorTile(cellKey)
    }

    private fun createDonorTileCalculators(): Map<CellLayerKind, DonorTileCalculator> {
        val layerTileMap = HashMap<CellLayerKind, MutableMap<CellKey, Tile>>()

        for (entity in getEntities(TILE_COMPONENT_LIST)) {
            val tile = TileComponent.getTile(entity) ?: continue

            val layerKind = KindComponent.getLayerKind(entity)

            layerTileMap.getOrPut(layerKind, ::HashMap)[CellComponent.getCellKey(entity)] = tile
        }

        return layerTileMap.mapValues {(_, tilesMap) -> DonorTileCalculator(tilesMap) }
    }

    companion object {
        val CELL_COMPONENT_LIST = listOf(
            CellComponent::class,
            KindComponent::class
        )

        val TILE_COMPONENT_LIST = listOf(
            CellComponent::class,
            KindComponent::class,
            TileComponent::class
        )
    }
}
