/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.tile

import org.jetbrains.letsPlot.gis.common.testUtils.HexParser
import org.jetbrains.letsPlot.gis.tileprotocol.GeometryCollection
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayerBuilder
import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.api.Services.bogusTileProvider
import org.jetbrains.letsPlot.livemap.config.createMapProjection
import org.jetbrains.letsPlot.livemap.core.Projections.mercator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsSystem
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.jetbrains.letsPlot.livemap.mapengine.basemap.*
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileDataParserImpl
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileLoadingSystem
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileLoadingSystem.TileResponseComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent
import org.junit.Test
import kotlin.reflect.KClass

class VectorTilesTest : LiveMapTestBase() {
    override val systemsOrder: List<KClass<out EcsSystem>>
        get() = listOf(TileLoadingSystem::class, SchedulerSystem::class)

    override fun setUp() {
        super.setUp()
        createEntity(
            "CellState",
            ViewportGridStateComponent(),
            StatisticsComponent()
        )

        val cellKey = CellKey("1")
        createEntity(
            "Tile",
            RequestTilesComponent(),
            KindComponent(BasemapLayerKind.WORLD),
            BasemapTileComponent(),
            TileResponseComponent().apply {
                tileData = listOf(
                    TileLayerBuilder().apply {
                        geometryCollection = GeometryCollection(
                            HexParser.parseHex(
                                "c600020128a7cbe7439ab8ad283426de03f905cc09b204fa059307810993049904e504a505d8018d02f103671e5fb3015fab01e70bf803164e1a721" +
                                        "4740e7408740474017205740d74117417721750134ab70180058c026d4a7a4a1d308001a40289013a1b1a0bb8014f86039c039203bf01aa017c463246340105ad12c7020a1" +
                                        "01c6b51182c44"
                            )
                        )
                    }.build()
                )
            },
            BasemapCellComponent(cellKey)
        )

        addSystem(TileLoadingSystem(1, bogusTileProvider(), componentManager))
    }

    @Test
    fun manyPoints() {
        update()

        update()
        update()
        update()
        update()
    }

    @Test
    fun parserTest() {
        val tileLayer = TileLayerBuilder().apply {
            geometryCollection = GeometryCollection(
                HexParser.parseHex(
                    "c600020128a7cbe7439ab8ad283426de03f905cc09b204fa059307810993049904e504a505d8018d02f103671e5fb3015fab01e70bf803164e1a721" +
                            "4740e7408740474017205740d74117417721750134ab70180058c026d4a7a4a1d308001a40289013a1b1a0bb8014f86039c039203bf01aa017c463246340105ad12c7020a1" +
                            "01c6b51182c44"
                )
            )
        }.build()

        val dataParserImpl = TileDataParserImpl(createMapProjection(mercator()))
        val microTask = dataParserImpl.parse(CellKey("1"), listOf(tileLayer))

        var i = 0
        while (microTask.alive()) {
            i++
            microTask.resume()
        }

        println("i = $i")
    }
}