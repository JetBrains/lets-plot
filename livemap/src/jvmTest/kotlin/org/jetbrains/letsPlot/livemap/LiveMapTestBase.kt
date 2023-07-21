/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package org.jetbrains.letsPlot.livemap

import org.jetbrains.letsPlot.livemap.core.ecs.ComponentManagerUtil
import org.jetbrains.letsPlot.livemap.stubs.LayerManagerStub
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.api.FeatureLayerBuilder
import org.jetbrains.letsPlot.livemap.config.createMapProjection
import org.jetbrains.letsPlot.livemap.core.Projections
import org.jetbrains.letsPlot.livemap.core.SystemTime
import org.jetbrains.letsPlot.livemap.core.ecs.*
import org.jetbrains.letsPlot.livemap.core.graphics.TextMeasurer
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskCooperativeExecutor
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapRenderContext
import org.jetbrains.letsPlot.livemap.mapengine.camera.MutableCamera
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.reflect.KClass
import kotlin.test.assertTrue

abstract class LiveMapTestBase {
    lateinit var componentManager: EcsComponentManager
    lateinit var liveMapContext: LiveMapContext
    private lateinit var mySystemTime: SystemTime
    private val mySystems = HashMap<KClass<out EcsSystem>, EcsSystem>()
    private var dt: Double = 0.0
    private var updateRepeatTimes: Int = 0
    private lateinit var featureLayerBuilder: FeatureLayerBuilder

    protected abstract val systemsOrder: List<KClass<out EcsSystem>>

    protected lateinit var myCamera: MutableCamera

    protected open val size = org.jetbrains.letsPlot.livemap.ClientPoint(500, 500)


    @Before
    open fun setUp() {
        componentManager = EcsComponentManager()
        myCamera = MutableCamera(componentManager)

        liveMapContext = mock(LiveMapContext::class.java)
        val projection = Projections.mercator()
        val mapProjection = createMapProjection(projection)
        val viewportHelper = ViewportHelper(mapProjection.mapRect, projection.cylindrical, myLoopY = false)

        val viewport = Viewport(viewportHelper, size, 1, 15)

        val mapRenderContext = mock(MapRenderContext::class.java)
        `when`(mapRenderContext.viewport).thenReturn(viewport)

        `when`(liveMapContext.mapProjection).thenReturn(mapProjection)
        `when`(liveMapContext.mapRenderContext).thenReturn(mapRenderContext)
        `when`(liveMapContext.camera).thenReturn(myCamera)

        featureLayerBuilder = FeatureLayerBuilder(
            componentManager,
            LayerManagerStub(),
            mapProjection,
            TextMeasurer(mock(Context2d::class.java))
        )

        mySystemTime = mock(SystemTime::class.java)
        `when`(liveMapContext.systemTime).thenReturn(mySystemTime)

        addSystem<EcsSystem>(
            SchedulerSystem(
                MicroTaskCooperativeExecutor(liveMapContext,
                    org.jetbrains.letsPlot.livemap.LiveMapTestBase.Companion.SCHEDULER_FRAME_TIME_LIMIT
                ),
                componentManager
            )
        )
    }

   inline fun <reified ComponentT : EcsComponent> getSingletonComponent(): ComponentT {
       return componentManager.getSingleton()
   }

    protected fun createEntity(name: String, vararg components: EcsComponent): EcsEntity {
        return componentManager.createEntity(name).apply {
            listOf(*components).forEach { addComponents { + it } }
        }
    }

    protected fun update(specs: Iterable<org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec>) {
        deltaTimeSpec().standard().apply()
        schedulerSpec().runAll().apply()
        repeatSpec().times(1).apply()

        specs.forEach(org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec::apply)

        while (updateRepeatTimes-- > 0) {
            systemsOrder.forEach { systemClass ->
                mySystems[systemClass]?.update(liveMapContext, dt)
            }
        }

        afterUpdateCleanup().forEach(org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec::apply)
    }

    /**
     * By default, delta time is 16 and scheduler runs all microthreads
     */
    protected fun update(vararg specs: org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec) {
        update(listOf(*specs))
    }

    protected open fun afterUpdateCleanup(): List<org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec> {
        return emptyList()
    }

    protected fun <T : EcsSystem> addSystem(system: T): T {
        system.init(liveMapContext)
        mySystems[system::class] = system
        return system
    }

    protected fun layers(block: FeatureLayerBuilder.() -> Unit) {
        featureLayerBuilder.apply(block)
    }


    fun getEntity(name: String): EcsEntity {
        return ComponentManagerUtil.getEntity(name, componentManager) ?: throw NoSuchElementException()
    }

    fun findEntity(name: String): Boolean {
        return ComponentManagerUtil.getEntity(name, componentManager) != null
    }

    protected fun getEntities(components: List<KClass<out EcsComponent>>): Collection<EcsEntity> {
        return componentManager.getEntities(components).toList()
    }

    protected inline fun <reified T : EcsComponent> getEntityComponent(name: String): T {
        return getEntity(name).get()
    }

    fun schedulerSpec(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.SchedulerSpec {
        return org.jetbrains.letsPlot.livemap.LiveMapTestBase.SchedulerSpec(this)
    }

    private fun deltaTimeSpec(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.DeltaTimeSpec {
        return org.jetbrains.letsPlot.livemap.LiveMapTestBase.DeltaTimeSpec(this)
    }

    internal fun repeatSpec(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.RepeatSpec {
        return org.jetbrains.letsPlot.livemap.LiveMapTestBase.RepeatSpec(this)
    }

    protected fun assertEntityOrigin(name: String, p: org.jetbrains.letsPlot.livemap.WorldPoint) {
        assertTrue { getEntityComponent<WorldOriginComponent>(name).origin == p }
    }

    abstract class MockSpec protected constructor(internal val testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) {

        protected val componentManager: EcsComponentManager
            get() = testBase.componentManager

        protected val liveMapContext: LiveMapContext
            get() = testBase.liveMapContext

        abstract fun apply()
    }

    protected class DeltaTimeSpec constructor(private val myTestBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) {

        internal fun standard(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return constant(16.0)
        }

        fun constant(dt: Double): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    testBase.dt = dt
                }
            }
        }
    }

    class SchedulerSpec internal constructor(private val myTestBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) {

        fun frameTime() : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(org.jetbrains.letsPlot.livemap.LiveMapTestBase.Companion.SCHEDULER_FRAME_TIME_LIMIT + 1L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun skipAll(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(org.jetbrains.letsPlot.livemap.LiveMapTestBase.Companion.SCHEDULER_FRAME_TIME_LIMIT + 1L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun runAll(): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(0L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun runTimes(vararg times: Long): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(times[0])
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(times[0], *times.drop(1).toTypedArray())
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }
    }

    class RepeatSpec(private val myTestBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) {

        operator fun times(times: Int): org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec {
            return object : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(myTestBase) {
                override fun apply() {
                    testBase.updateRepeatTimes = times
                }
            }
        }
    }

    companion object {
        private const val SCHEDULER_FRAME_TIME_LIMIT: Long = 5000
    }
}