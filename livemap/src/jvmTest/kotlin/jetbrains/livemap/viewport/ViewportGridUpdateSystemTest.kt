/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.viewport

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.config.createMapProjection
import jetbrains.livemap.core.Projections
import jetbrains.livemap.mapengine.camera.CameraInputSystem
import jetbrains.livemap.mapengine.viewport.CellKey
import jetbrains.livemap.mapengine.viewport.ViewportGridStateComponent
import jetbrains.livemap.mapengine.viewport.ViewportGridUpdateSystem
import jetbrains.livemap.mapengine.viewport.ViewportPositionUpdateSystem
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertTrue

class ViewportGridUpdateSystemTest : LiveMapTestBase() {
    override val systemsOrder = listOf(
        CameraInputSystem::class,
        ViewportPositionUpdateSystem::class,
        ViewportGridUpdateSystem::class
    )

    @Before
    override fun setUp() {
        super.setUp()

        val mapProjection = createMapProjection(Projections.mercator())

        Mockito.`when`(liveMapContext.mapProjection).thenReturn(mapProjection)

        addSystem(CameraInputSystem(componentManager))
        addSystem(ViewportPositionUpdateSystem(componentManager))
        addSystem(ViewportGridUpdateSystem(componentManager))
    }

    @Ignore("What test this test")
    @Test
    fun onUpdateShouldReUseExistingCellKeyInstances() {

        val cellA = CellKey("1111")
        val cellB = CellKey("0000")

        update(
            Mocks.camera(this)
                .position(WorldPoint(0, 0))
                .zoom(4.0)
        )

        val cellsAtPos00 = getEntityComponent<ViewportGridStateComponent>("ViewportGrid").visibleCells
        assert(cellsAtPos00.containsAll(listOf(cellA, cellB)))


        // Move camera so cell 0001 get into view
        update(
            Mocks.camera(this)
                .position(WorldPoint(8, 0))
        )

        val cellsAtPos80 = getEntityComponent<ViewportGridStateComponent>("ViewportGrid").visibleCells
        assert(cellsAtPos80.containsAll(listOf(cellA, cellB, CellKey("0001"))))

        // Check with equals to find common keys (should be 00 and 03)
        val commonCells = cellsAtPos00.intersect(cellsAtPos80)

        assertTrue { commonCells.size == 2 }

        val cells0003 = ArrayList<CellKey>()

        // Ref check for purpose - no extra allocations
        commonCells.forEach { key ->
            if (key === cellB) {
                cells0003.add(key)
            }

            if (key === cellA) {
                cells0003.add(key)
            }
        }

        assertTrue { cells0003.containsExactlyInAnyOrders(listOf(cellB, cellA)) }
    }

    private fun <T> Collection<T>.containsExactlyInAnyOrders(values: Collection<T> ): Boolean {
        return containsAll(values) && size == values.size
    }

    @Test
    fun removeQuadsReferencedTwoCells() {
        val state = ViewportGridStateComponent()

        QuadsUpdate(state)
            .request("0", "1", "1")
            .sync()

        assertTrue { state.quadsRefCounter.keys.containsExactlyInAnyOrders(quads("0", "1")) }
        assertTrue { state.quadsToRemove.isEmpty() }

        QuadsUpdate(state)
            .remove("1")
            .sync()

        assertTrue { state.quadsRefCounter.keys.containsExactlyInAnyOrders(quads("0", "1")) }

        QuadsUpdate(state)
            .remove("1")
            .sync()

        assertTrue { state.quadsRefCounter.keys.containsExactlyInAnyOrders(quads("0")) }
        assertTrue { state.quadsToRemove.containsExactlyInAnyOrders(quads("1")) }
    }

    fun quads(vararg keys: String): List<QuadKey<LonLat>> = listOf(*keys).map(::QuadKey)

    inner class QuadsUpdate(private val myState: ViewportGridStateComponent) {
        private var myToRequest: List<QuadKey<LonLat>> = emptyList()
        private var myToRemove: List<QuadKey<LonLat>> = emptyList()

        fun request(vararg quadStrings: String): QuadsUpdate {
            myToRequest = quads(*quadStrings)
            return this
        }

        fun remove(vararg quadStrings: String): QuadsUpdate {
            myToRemove = quads(*quadStrings)
            return this
        }

        fun sync() {
            ViewportGridUpdateSystem.syncQuads(myState, myToRequest, myToRemove)
            myToRemove = emptyList()
            myToRequest = emptyList()
        }
    }
}