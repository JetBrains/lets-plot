/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.effects

import jetbrains.datalore.base.projectionGeometry.LineString
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.gis.geoprotocol.GeometryUtil.asLineString
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.projections.Client
import kotlin.math.sqrt


object GrowingPath {

    private fun length(p1: Vec<*>, p2: Vec<*>): Double {
        val x = p2.x - p1.x
        val y = p2.y - p1.y
        return sqrt(x * x + y * y)
    }

    class GrowingPathEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<EcsContext>(componentManager) {

        override fun updateImpl(context: EcsContext, dt: Double) {
            for (entity in getEntities(COMPONENT_TYPES)) {
                val path = asLineString(entity.get<ScreenGeometryComponent>().geometry)

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
                ScreenGeometryComponent::class,
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

        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains(ScreenGeometryComponent::class)) {
                return
            }

            val styleComponent = entity.get<StyleComponent>()
            val geometry = entity.get<ScreenGeometryComponent>().geometry
            val growingPath = entity.get<GrowingPathEffectComponent>()

            ctx.setStrokeStyle(styleComponent.strokeColor)
            ctx.setLineWidth(styleComponent.strokeWidth)
            ctx.beginPath()

            for (polygon in geometry) {
                val ring = polygon[0]
                var viewCoord: Vec<Client> = ring[0]
                ctx.moveTo(viewCoord.x, viewCoord.y)

                for (i in 1..growingPath.endIndex) {
                    viewCoord = ring[i]
                    ctx.lineTo(viewCoord.x, viewCoord.y)
                }

                growingPath.interpolatedPoint?.let { ctx.lineTo(it.x, it.y) }
            }
            ctx.stroke()
        }
    }
}
