/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.effects

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.LineString
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathEffectComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathEffectSystem
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathRenderer
import jetbrains.livemap.core.animation.Animation.Direction
import jetbrains.livemap.core.animation.Animation.Loop
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.core.util.EasingFunctions.LINEAR
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import org.mockito.Mockito
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

    private fun p(x: Double, y: Double): Vec<World> {
        return explicitVec(x, y)
    }

    private fun index(i: Int): EffectState {
        return EffectState(i, null)
    }

    private fun indices(vararg v: Int): List<EffectState> {
        return v.map(::index)
    }

    private fun interpolated(i: Int, p: Vec<Client>): EffectState {
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
                UNSUPPORTED()
            }
        })

        myGrowingPathEffectSystem = GrowingPathEffectSystem(myComponentManager)

        myAnimationComponent = AnimationComponent().apply {
            loop = Loop.KEEP_DIRECTION
            direction = Direction.FORWARD
            easingFunction = LINEAR
        }

        myGrowingPathEffectComponent = GrowingPathEffectComponent().apply {
            animationId = myComponentManager
                .createEntity("animation")
                .add(myAnimationComponent).id
        }
    }

    private fun doAnimation(progress: Double) {
        myAnimationComponent.progress = progress
        myGrowingPathEffectSystem.update(myEcsContext, 0.0)
        myEffectState.add(
            EffectState(
                myGrowingPathEffectComponent.endIndex,
                myGrowingPathEffectComponent.interpolatedPoint?.roundDecimals(3)
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

    private fun createEffect(vararg points: Vec<World>) {
        myComponentManager.createEntity("effect")
            .addComponents {
                + myGrowingPathEffectComponent
                + WorldOriginComponent(p(0.0, 0.0))
                + WorldGeometryComponent().apply {
                    geometry = Geometry.of(LineString.of(*points))
                }
                + ParentLayerComponent(
                    myComponentManager.createEntity("parent layer").id
                )
            }
    }

    @Test
    fun rendererTest() {
        val render = GrowingPathRenderer()
        val pathEntity = myComponentManager.createEntity("path_entity")
            .addComponents {
                + ParentLayerComponent(0)
                + GrowingPathEffectComponent().apply {
                    endIndex = 3
                    interpolatedPoint = explicitVec(3.5, 3.5)
                }
                + WorldOriginComponent(p(0.0, 0.0))
                + WorldGeometryComponent().apply {
                    geometry = Geometry.of(LineString.of(p(0.0, 0.0), p(1.0, 1.0), p(2.0, 2.0), p(3.0, 3.0), p(4.0, 4.0)))
                }
                + RenderableComponent().apply {
                    renderer = render
                }
                + ChartElementComponent().apply {
                    fillColor = Color.BLACK
                    strokeColor = Color.BLACK
                    strokeWidth = 1.0
                }
            }

        val context2d = Mockito.mock(Context2d::class.java)

        render.render(pathEntity, context2d, RenderHelper(Mockito.mock(Viewport::class.java)))

        Mockito.verify(context2d).moveTo(0.0, 0.0)
        Mockito.verify(context2d).lineTo(1.0, 1.0)
        Mockito.verify(context2d).lineTo(2.0, 2.0)
        Mockito.verify(context2d).lineTo(3.0, 3.0) // end point
        Mockito.verify(context2d).lineTo(3.5, 3.5) // interpolated point;
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
                interpolated(0, explicitVec(0.0, 75.0)),
                interpolated(1, explicitVec(0.0, 150.0)),
                interpolated(1, explicitVec(0.0, 225.0)),
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
                interpolated(0, explicitVec(0.0, 100.0)),
                interpolated(0, explicitVec(0.0, 200.0)),
                index(1)
            ),
            myEffectState
        )
    }

    internal data class EffectState(private val endIndex: Int, private val endPoint: Vec<Client>?)
}

private fun <TypeT> Vec<TypeT>.roundDecimals(places: Int): Vec<TypeT> {
    return (10.ipow(places)).let {
        explicitVec(
            x.roundDecimals(it),
            y.roundDecimals(it)
        )
    }
}

private fun Double.roundDecimals(places: Int) = round(this * places) / places
private fun Int.ipow(n: Int) = this.toDouble().pow(n).toInt()
