/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.LineString
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.core.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import kotlin.math.sqrt


object GrowingPathEffect {

    private fun length(p1: Vec<*>, p2: Vec<*>): Double {
        val x = p2.x - p1.x
        val y = p2.y - p1.y
        return sqrt(x * x + y * y)
    }

    class GrowingPathEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<EcsContext>(componentManager) {

        override fun updateImpl(context: EcsContext, dt: Double) {
            for (entity in getEntities(COMPONENT_TYPES)) {
                val path = entity.get<WorldGeometryComponent>().geometry.multiLineString.single()

                val effectComponent = entity.get<GrowingPathEffectComponent>()
                if (effectComponent.lengthIndex.isEmpty()) {
                    effectComponent.init(path)
                }

                val animation = getEntityById(effectComponent.animationId) ?: return

                calculateEffectState(effectComponent, path, animation.get<AnimationComponent>().progress)

                tagDirtyParentLayer(entity)
            }
        }

        private fun GrowingPathEffectComponent.init(path: LineString<*>) {
            var l = 0.0
            lengthIndex = ArrayList<Double>(path.size).apply {
                add(0.0)

                for (i in 1 until path.size) {
                    l += length(path[i - 1], path[i])
                    add(l)
                }
            }

            length = l
        }

        private fun calculateEffectState(
            effectComponent: GrowingPathEffectComponent,
            path: LineString<*>,
            progress: Double
        ) {
            val lengthIndex = effectComponent.lengthIndex
            val length = effectComponent.length

            val current = length * progress
            var index = lengthIndex.binarySearch(current)
            if (index >= 0) {
                effectComponent.endIndex = index
                effectComponent.interpolatedPoint = null
                return
            }

            index = index.inv() - 1

            if (index == lengthIndex.size - 1) {
                effectComponent.endIndex = index
                effectComponent.interpolatedPoint = null
                return
            }

            val l1 = lengthIndex[index]
            val l2 = lengthIndex[index + 1]
            val dl = l2 - l1

            if (dl > 2.0) {
                val dp = dl / length
                val p1 = l1 / length
                val p = (progress - p1) / dp

                val v1 = path[index]
                val v2 = path[index + 1]

                effectComponent.endIndex = index
                effectComponent.interpolatedPoint = explicitVec(v1.x + (v2.x - v1.x) * p, v1.y + (v2.y - v1.y) * p)
            } else {
                effectComponent.endIndex = index
                effectComponent.interpolatedPoint = null
            }
        }

        companion object {

            private val COMPONENT_TYPES = listOf(
                GrowingPathEffectComponent::class,
                WorldGeometryComponent::class,
                WorldOriginComponent::class,
                ParentLayerComponent::class
            )
        }
    }

    class GrowingPathEffectComponent : EcsComponent {
        var animationId: Int = 0
        var lengthIndex: List<Double> = emptyList()
        var length: Double = 0.0
        var endIndex: Int = 0
        var interpolatedPoint: Vec<Client>? = null // can be null if no need in point interpolation
    }

    class GrowingPathRenderer : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
            val chartElement = entity.get<ChartElementComponent>()
            val lineString = entity.get<WorldGeometryComponent>().geometry.multiLineString.single()
            val growingPath = entity.get<GrowingPathEffectComponent>()

            ctx.save()
            ctx.scale(renderHelper.zoomFactor)
            ctx.beginPath()

            var viewCoord: Vec<World> = lineString[0]
            ctx.moveTo(viewCoord.x, viewCoord.y)

            for (i in 1..growingPath.endIndex) {
                viewCoord = lineString[i]
                ctx.lineTo(viewCoord.x, viewCoord.y)
            }

            growingPath.interpolatedPoint?.let { ctx.lineTo(it.x, it.y) }
            ctx.restore()

            ctx.setStrokeStyle(chartElement.strokeColor)
            ctx.setLineWidth(chartElement.strokeWidth)
            ctx.stroke()
        }
    }
}
