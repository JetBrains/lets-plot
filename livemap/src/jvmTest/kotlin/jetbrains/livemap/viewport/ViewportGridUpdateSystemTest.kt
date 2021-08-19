/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.viewport

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.livemap.LiveMapConstants
import jetbrains.livemap.core.ecs.EcsSystem
import jetbrains.livemap.core.projections.ProjectionType
import jetbrains.livemap.projection.Coordinates
import jetbrains.livemap.projection.WorldRectangle
import jetbrains.livemap.projection.createMapProjection
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.viewport.ViewportGridStateComponent
import jetbrains.livemap.viewport.ViewportGridUpdateSystem
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.reflect.KClass
import kotlin.test.assertTrue

class ViewportGridUpdateSystemTest : LiveMapTestBase() {
    override val systemsOrder: List<KClass<out EcsSystem>> = listOf(
        ViewportGridUpdateSystem::class
    )

    @Before
    override fun setUp() {
        super.setUp()

        val mapRect = WorldRectangle(
            Coordinates.ZERO_WORLD_POINT, explicitVec(
                LiveMapConstants.TILE_PIXEL_SIZE,
                LiveMapConstants.TILE_PIXEL_SIZE
            )
        )
        val mapProjection =
            createMapProjection(ProjectionType.MERCATOR, mapRect)

        Mockito.`when`(liveMapContext.mapProjection).thenReturn(mapProjection)

        addSystem(ViewportGridUpdateSystem(componentManager))
    }

    @Test
    fun onUpdateShouldReUseExistingCellKeyInstances() {
        val cell03 = CellKey("03")
        val cell00 = CellKey("00")

        update(
            StateSpec(this).apply {
                visibleCells = listOf(
                    CellKey("01"),
                    CellKey("02"), cell03, cell00)
            }
        )

        val viewportGridState = getEntityComponent<ViewportGridStateComponent>("ViewportGrid")
        val firstCallCellKeys = viewportGridState.visibleCells

        // Create new 03 and 00 CellKeys intances in request
        update(
            StateSpec(this).apply {
                visibleCells = listOf(
                    CellKey("11"),
                    CellKey("12"),
                    CellKey("03"),
                    CellKey("00")
                )
            }
        )

        val secondCallCellKeys = viewportGridState.visibleCells

        // Check with equals to find common keys (should be 00 and 03)
        val common = firstCallCellKeys.intersect(secondCallCellKeys)

        assertTrue { common.size == 2 }

        val cells0003 = ArrayList<CellKey>()

        // Check pointers
        common.forEach { key ->
            if (key === cell00) {
                cells0003.add(key)
            }

            if (key === cell03) {
                cells0003.add(key)
            }
        }

        assertTrue { cells0003.containsExactlyInAnyOrders(listOf(cell00, cell03)) }
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

    class StateSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {
        var visibleCells: List<CellKey> = emptyList()

        override fun apply() {
            Mockito.`when`(testBase.liveMapContext.mapRenderContext.viewport.visibleCells)
                .thenReturn(HashSet(visibleCells))
        }
    }

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