/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.tiles.components.CellStateComponent
import jetbrains.livemap.tiles.components.StatisticsComponent
import kotlin.reflect.KClass

class CellStateUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("CellState")
            .addComponent(StatisticsComponent())
            .addComponent(CellStateComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val stateEntity = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS)
        val cellState: CellStateComponent = stateEntity.get()

        cellState.update(context.mapRenderContext.viewport.visibleCells)

        syncQuads(
            cellState,
            toQuads(cellState.requestCells, context.mapProjection),
            toQuads(cellState.cellsToRemove, context.mapProjection)
        )
    }

    private fun toQuads(cellKeys: Set<CellKey>, mapProjection: MapProjection): List<QuadKey> {
        return cellKeys.flatMap { convertCellKeyToQuadKeys(mapProjection, it) }
    }

    companion object {

        internal val CELL_STATE_REQUIRED_COMPONENTS: List<KClass<out EcsComponent>> = listOf(
            CellStateComponent::class,
            StatisticsComponent::class
        )

        internal fun syncQuads(cellState: CellStateComponent, newQuads: List<QuadKey>, obsoleteQuads: List<QuadKey>) {
            val quadsRefCounter = cellState.quadsRefCounter

            cellState.quadsToAdd = HashSet<QuadKey>().apply {
                for (newQuad in newQuads) {
                    if (incRef(quadsRefCounter, newQuad) == 1) {
                        add(newQuad)
                    }
                }
            }

            cellState.quadsToRemove = HashSet<QuadKey>().apply {
                for (obsoleteQuad in obsoleteQuads) {
                    if (decRef(quadsRefCounter, obsoleteQuad) == 0) {
                        add(obsoleteQuad)
                    }
                }
            }
        }

        private fun incRef(counter: MutableMap<QuadKey, Int>, quad: QuadKey): Int {
            return ((counter[quad] ?: 0) + 1).also {
                counter[quad] = it
            }
        }

        private fun decRef(counter:MutableMap<QuadKey, Int>, quad:QuadKey):Int {
            counter[quad]?.let {
                if (counter.put(quad, it - 1) == 1) {
                    counter.remove(quad)
                    return 0
                }
                return it - 1
            } ?: throw IllegalStateException()
        }
    }
}