package jetbrains.livemap.effects

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.AnyLineString
import jetbrains.datalore.base.projectionGeometry.AnyPoint
import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.datalore.maps.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.gis.geoprotocol.GeometryUtil.asLineString
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.projections.Client
import kotlin.math.sqrt


object GrowingPath {

    private fun length(p1: AnyPoint, p2: AnyPoint): Double {
        val x = p2.x - p1.x
        val y = p2.y - p1.y
        return sqrt(x * x + y * y)
    }

    class GrowingPathEffectSystem(componentManager: EcsComponentManager) :
        AbstractSystem<EcsContext>(componentManager) {

        protected override fun updateImpl(context: EcsContext, dt: Double) {
            for (entity in getEntities(COMPONENT_TYPES)) {
                val path = asLineString(entity.get<ScreenGeometryComponent>().geometry)

                val effectComponent = GrowingPathEffectComponent[entity]
                if (effectComponent.getLengthIndex() == null) {
                    initComponent(effectComponent, path)
                }

                val animation = getEntityById(effectComponent.getAnimationId()) ?: return

                calculateEffectState(effectComponent, path, animation.get<AnimationComponent>().progress)

                tagDirtyParentLayer(entity)
            }
        }

        private fun calculateEffectState(
            effectComponent: GrowingPathEffectComponent,
            path: AnyLineString,
            progress: Double
        ) {
            val lengthIndex = effectComponent.getLengthIndex()!!
            val length = effectComponent.getLength()

            val current = length * progress
            var index = lengthIndex.binarySearch(current)
            if (index >= 0) {
                effectComponent.setEndIndex(index)
                effectComponent.setInterpolatedPoint(null)
                return
            }

            index = index.inv() - 1

            if (index == lengthIndex.size - 1) {
                effectComponent.setEndIndex(index)
                effectComponent.setInterpolatedPoint(null)
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

                effectComponent.setEndIndex(index)
                effectComponent.setInterpolatedPoint(DoubleVector(v1.x + (v2.x - v1.x) * p, v1.y + (v2.y - v1.y) * p))
            } else {
                effectComponent.setEndIndex(index)
                effectComponent.setInterpolatedPoint(null)
            }
        }

        companion object {

            private val COMPONENT_TYPES = listOf(
                GrowingPathEffectComponent::class,
                ScreenGeometryComponent::class,
                ParentLayerComponent::class
            )

            private fun initComponent(effectComponent: GrowingPathEffectComponent, path: AnyLineString) {
                val lengthIndex = ArrayList<Double>(path.size)
                lengthIndex.add(0.0)
                var l = 0.0
                for (i in 1 until path.size) {
                    l += length(path[i - 1], path[i])
                    lengthIndex.add(l)
                }

                effectComponent.setLengthIndex(lengthIndex)
                effectComponent.setLength(l)
            }
        }
    }

    class GrowingPathEffectComponent : EcsComponent {

        private var myAnimationId: Int = 0
        private var myLengthIndex: List<Double>? = null
        private var myLength: Double = 0.toDouble()
        private var myEndIndex: Int = 0
        private var myInterpolatedPoint: DoubleVector? = null // can be null if no need in point interpolation

        internal fun getEndIndex(): Int {
            return myEndIndex
        }

        internal fun setEndIndex(endIndex: Int): GrowingPathEffectComponent {
            myEndIndex = endIndex
            return this
        }

        internal fun getInterpolatedPoint(): DoubleVector? {
            return myInterpolatedPoint
        }

        internal fun setInterpolatedPoint(endPoint: DoubleVector?): GrowingPathEffectComponent {
            myInterpolatedPoint = endPoint
            return this
        }


        internal fun getLengthIndex(): List<Double>? {
            return myLengthIndex
        }

        internal fun setLengthIndex(lengthIndex: List<Double>): GrowingPathEffectComponent {
            myLengthIndex = lengthIndex
            return this
        }

        fun getLength(): Double {
            return myLength
        }

        fun setLength(length: Double): GrowingPathEffectComponent {
            myLength = length
            return this
        }

        internal fun getAnimationId(): Int {
            return myAnimationId
        }

        fun setAnimationId(entityId: Int): GrowingPathEffectComponent {
            myAnimationId = entityId
            return this
        }

        companion object {
            operator fun get(entity: EcsEntity): GrowingPathEffectComponent {
                return entity.get<GrowingPathEffectComponent>()
            }
        }
    }

    class GrowingPathRenderer : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains(ScreenGeometryComponent::class)) {
                return
            }

            val styleComponent = entity.get<StyleComponent>()
            val geometry = entity.get<ScreenGeometryComponent>().geometry

            val growingPath = GrowingPathEffectComponent[entity]

            ctx.setStrokeStyle(styleComponent.strokeColor)
            ctx.setLineWidth(styleComponent.strokeWidth)
            ctx.beginPath()

            for (polygon in geometry.asMultipolygon()) {
                val ring = polygon.get(0)
                var viewCoord: Typed.Vec<Client> = ring.get(0)
                ctx.moveTo(viewCoord.x, viewCoord.y)

                for (i in 1..growingPath.getEndIndex()) {
                    viewCoord = ring.get(i)
                    ctx.lineTo(viewCoord.x, viewCoord.y)
                }

                growingPath.getInterpolatedPoint()?.let { ctx.lineTo(it.x, it.y) }
            }
            ctx.stroke()
        }
    }
}
