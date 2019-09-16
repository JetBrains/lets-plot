package jetbrains.livemap.effects

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.maps.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.core.animation.Animation.Direction
import jetbrains.livemap.core.animation.Animation.Loop
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.effects.GrowingPath.GrowingPathEffectComponent
import jetbrains.livemap.effects.GrowingPath.GrowingPathEffectSystem
import jetbrains.livemap.effects.GrowingPath.GrowingPathRenderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setFillColor
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.projections.Client
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GrowingPathTest {

    private val myEffectState = ArrayList<EffectState>()
    private lateinit var myGrowingPathEffectSystem: GrowingPathEffectSystem
    private lateinit var myComponentManager: EcsComponentManager
    private lateinit var myEcsContext: EcsContext
    private lateinit var myAnimationComponent: AnimationComponent
    private lateinit var myGrowingPathEffectComponent: GrowingPathEffectComponent

    private fun p(x: Double, y: Double): Typed.Vec<Client> {
        return Typed.Vec(x, y)
    }

    private fun index(i: Int): EffectState {
        return EffectState(i, null)
    }

    private fun indices(vararg v: Int): List<EffectState> {
        return v.map { index(it) }
    }

    private fun interpolated(i: Int, p: DoubleVector): EffectState {
        return EffectState(i, p)
    }

    @BeforeTest
    fun setUp() {
        myComponentManager = EcsComponentManager()
        myEcsContext = EcsContext(object : MouseEventSource {
            override fun addEventHandler(
                eventSpec: MouseEventSpec,
                eventHandler: EventHandler<MouseEvent>
            ): Registration {
                TODO("not implemented")
            }
        })

        myGrowingPathEffectSystem = GrowingPathEffectSystem(myComponentManager)

        myAnimationComponent = AnimationComponent().apply {
            loop = Loop.KEEP_DIRECTION
            direction = Direction.FORWARD
            easingFunction = Animations.LINEAR
        }

        myGrowingPathEffectComponent = GrowingPathEffectComponent()
            .setAnimationId(
                myComponentManager
                    .createEntity("animation")
                    .addComponent(myAnimationComponent).id
            )
    }

    private fun doAnimation(progress: Double) {
        myAnimationComponent.progress = progress
        myGrowingPathEffectSystem.update(myEcsContext, 0.0)
        myEffectState.add(
            EffectState(
                myGrowingPathEffectComponent.getEndIndex(),
                myGrowingPathEffectComponent.getInterpolatedPoint()?.roundDecimals(3)
            )
        )
    }

    private fun doAnimationSequence(progress: List<Double>) {
        for (v in progress) {
            doAnimation(v)
        }
    }

    private fun steps(n: Int): List<Double> {
        val progress = ArrayList<Double>()
        for (i in 0 until n) {
            progress.add((i.toDouble() / (n - 1)))
        }

        return progress
    }

    private fun createGeometry(vararg points: Typed.Vec<Client>): TypedGeometry<Client> {
        return TypedGeometry.create(Typed.MultiPolygon(listOf(Typed.Polygon(listOf(Typed.Ring(listOf(*points)))))))
    }

    private fun createEffect(vararg points: Typed.Vec<Client>) {
        myComponentManager.createEntity("effect")
            .addComponent(myGrowingPathEffectComponent)
            .addComponent(
                ScreenGeometryComponent().apply {
                    geometry = createGeometry(*points)
                }
            )
            .addComponent(
                ParentLayerComponent(
                    myComponentManager.createEntity("parent layer").id
                )
            )
    }

    @Test
    fun rendererTest() {
        val renderer = GrowingPathRenderer()
        val path_entity = myComponentManager.createEntity("path_entity")
            .addComponent(
                GrowingPathEffectComponent()
                    .setEndIndex(3)
                    .setInterpolatedPoint(DoubleVector(3.5, 3.5))
            )
            .addComponent(
                ScreenGeometryComponent().apply {
                    geometry = createGeometry(p(0.0, 0.0), p(1.0, 1.0), p(2.0, 2.0), p(3.0, 3.0), p(4.0, 4.0))
                }
            )
            .addComponent(
                (StyleComponent().apply {
                    setFillColor(Color.BLACK)
                    setStrokeColor(Color.BLACK)
                    strokeWidth = 1.0
                }
                        )
            )

        //val context2d = Mockito.mock(Context2d::class)

        //renderer.render(path_entity, context2d)

        //Mockito.verify(context2d).moveTo(0, 0)
        //Mockito.verify(context2d).lineTo(1, 1)
        //Mockito.verify(context2d).lineTo(2, 2)
        //Mockito.verify(context2d).lineTo(3, 3) // end point
        //Mockito.verify(context2d).lineTo(3.5, 3.5) // interpolated point;
    }

    @Test
    fun whenProgressPointsExactPoint_ShouldReturnOnlyIndex() {
        createEffect(p(0.0, 0.0), p(0.0, 1.0), p(0.0, 2.0), p(0.0, 3.0), p(0.0, 4.0))
        doAnimationSequence(steps(5))
        assertEquals(indices(0, 1, 2, 3, 4), myEffectState)
    }

    @Test
    fun whenProgressPointsIntoShortSegment_ShouldReturnOnlyIndex() {
        createEffect(p(0.0, 0.0), p(0.0, 1.0), p(0.0, 2.0), p(0.0, 3.0), p(0.0, 4.0))
        doAnimationSequence(steps(8))
        assertEquals(indices(0, 0, 1, 1, 2, 2, 3, 4), myEffectState)
    }

    @Test
    fun whenProgressPointsIntoLongSegment_ShouldReturnIndexAndInterpolatedPoint() {
        createEffect(p(0.0, 0.0), p(0.0, 100.0), p(0.0, 300.0))
        doAnimationSequence(steps(5))
        assertEquals(
            listOf(
                index(0),
                interpolated(0, DoubleVector(0.0, 75.0)),
                interpolated(1, DoubleVector(0.0, 150.0)),
                interpolated(1, DoubleVector(0.0, 225.0)),
                index(2)
            ),
            myEffectState
        )
    }

    @Test
    fun whenProgressPointsIntoLongSegment_WithOnlyTwoPointsLength_ShouldReturnIndexAndInterpolatedPoint() {
        createEffect(p(0.0, 0.0), p(0.0, 300.0))
        doAnimationSequence(steps(4))
        assertEquals(
            listOf(
                index(0),
                interpolated(0, DoubleVector(0.0, 100.0)),
                interpolated(0, DoubleVector(0.0, 200.0)),
                index(1)
            ),
            myEffectState
        )
    }

    internal data class EffectState(private val endIndex: Int, private val endPoint: DoubleVector?) {

        companion object {
            fun vectorEquals(v1: DoubleVector?, v2: DoubleVector?): Boolean {
                if (v1 == null && v2 == null) {
                    return true
                }

                return if (v1 != null && v2 != null) {
                    doubleEquals(v1.x, v2.x) && doubleEquals(v1.y, v2.y)
                } else false

            }

            fun doubleEquals(v1: Double, v2: Double): Boolean {
                return abs(v1 - v2) < 0.0001
            }
        }
    }
}

private fun DoubleVector.roundDecimals(places: Int): DoubleVector {
    return (10.ipow(places)).let { DoubleVector(x.roundDecimals(it), y.roundDecimals(it)) }
}

private fun Double.roundDecimals(places: Int) = round(this * places) / places
private fun Int.ipow(n: Int) = this.toDouble().pow(n).toInt()
