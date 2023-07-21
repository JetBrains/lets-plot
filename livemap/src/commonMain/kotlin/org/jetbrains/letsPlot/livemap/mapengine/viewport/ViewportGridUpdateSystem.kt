/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.viewport

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.basemap.StatisticsComponent
import kotlin.reflect.KClass

class ViewportGridUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun initImpl(context: LiveMapContext) {
        createEntity("ViewportGrid")
            .addComponents {
                +StatisticsComponent()
                + ViewportGridStateComponent()
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val stateEntity = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS)
        val viewportGridState: ViewportGridStateComponent = stateEntity.get()

        viewportGridState.update(context.mapRenderContext.viewport.visibleCells)

        syncQuads(
            viewportGridState,
            toQuads(viewportGridState.cellsToLoad, context.mapProjection),
            toQuads(viewportGridState.cellsToRemove, context.mapProjection)
        )
    }

    private fun toQuads(cellKeys: Set<CellKey>, mapProjection: MapProjection): List<QuadKey<LonLat>> {
        return cellKeys.flatMap {
            convertCellKeyToQuadKeys(
                mapProjection,
                it
            )
        }
    }

    companion object {

        internal val CELL_STATE_REQUIRED_COMPONENTS: List<KClass<out EcsComponent>> = listOf(
            ViewportGridStateComponent::class,
            StatisticsComponent::class
        )

        internal fun syncQuads(viewportGridState: ViewportGridStateComponent, newQuads: List<QuadKey<LonLat>>, obsoleteQuads: List<QuadKey<LonLat>>) {
            val quadsRefCounter = viewportGridState.quadsRefCounter

            viewportGridState.quadsToLoad = HashSet<QuadKey<LonLat>>().apply {
                for (newQuad in newQuads) {
                    if (incRef(quadsRefCounter, newQuad) == 1) {
                        add(newQuad)
                    }
                }
            }

            viewportGridState.quadsToRemove = HashSet<QuadKey<LonLat>>().apply {
                for (obsoleteQuad in obsoleteQuads) {
                    if (decRef(quadsRefCounter, obsoleteQuad) == 0) {
                        add(obsoleteQuad)
                    }
                }
            }
        }

        private fun incRef(counter: MutableMap<QuadKey<LonLat>, Int>, quad: QuadKey<LonLat>): Int {
            return ((counter[quad] ?: 0) + 1).also {
                counter[quad] = it
            }
        }

        private fun decRef(counter:MutableMap<QuadKey<LonLat>, Int>, quad: QuadKey<LonLat>):Int {
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