package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.Utils.diff
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil.convertCellKeyToQuadKeys
import kotlin.reflect.KClass

class CellStateUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("CellState")
            .addComponent(Components.StatisticsComponent())
            .addComponent(Components.CellStateComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val stateEntity = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS)
        val cellState = Components.CellStateComponent.get(stateEntity)
        run {
            val existingCells = cellState.visibleCells
            val visibleCells = context.mapRenderContext.viewProjection.visibleCells

            cellState.visibleCells = visibleCells
            cellState.requestCells = diff(visibleCells, existingCells)
            cellState.cellsToRemove = diff(existingCells, visibleCells)
        }

        run {
            val mapProjection = context.mapProjection
            val newQuads = toQuads(cellState.requestCells, mapProjection)
            val obsoleteQuads = toQuads(cellState.cellsToRemove, mapProjection)
            syncQuads(cellState, newQuads, obsoleteQuads)
        }
    }

    private fun toQuads(cellKeys: Set<CellKey>, mapProjection: MapProjection): List<QuadKey> {
        return ArrayList<QuadKey>().apply {
            for (cellKey in cellKeys) {
                addAll(convertCellKeyToQuadKeys(mapProjection, cellKey))
            }
        }
    }

    companion object {

        internal val CELL_STATE_REQUIRED_COMPONENTS: List<KClass<out EcsComponent>> = listOf(
            Components.CellStateComponent::class,
            Components.StatisticsComponent::class
        )

        internal fun syncQuads(cellState: Components.CellStateComponent, newQuads: List<QuadKey>, obsoleteQuads: List<QuadKey>) {
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

            // if (!counter.containsKey(quad)) {
            //     counter[quad] = 1
            //     return 1
            // } else {
            //     return counter.put(quad, counter[quad] + 1)!! + 1
            // }
        }

        private fun decRef(counter: MutableMap<QuadKey, Int>, quad: QuadKey): Int {
            return ((counter[quad] ?: error("")) - 1).also {
                if (it == 0) counter.remove(quad)
            }

           // if (!counter.containsKey(quad)) {
           //     throw IllegalStateException()
           // } else {
           //     val old = counter.put(quad, counter[quad] - 1)!!
           //     if (old == 1) {
           //         counter.remove(quad)
           //         return 0
           //     }
           //     return old - 1
           // }
        }
    }
}