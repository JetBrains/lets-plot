/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap

import jetbrains.datalore.jetbrains.livemap.core.ecs.ComponentManagerUtil
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.config.WORLD_RECTANGLE
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.multitasking.MicroTaskCooperativeExecutor
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapRenderContext
import jetbrains.livemap.mapengine.camera.MutableCamera
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import org.junit.Before
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.reflect.KClass
import kotlin.test.assertTrue

abstract class LiveMapTestBase {
    lateinit var componentManager: EcsComponentManager
    lateinit var liveMapContext: LiveMapContext
    private lateinit var mySystemTime: SystemTime
    private val mySystems = HashMap<KClass<out EcsSystem>, EcsSystem>()
    private var dt: Double = 0.0
    private var updateRepeatTimes: Int = 0

    protected abstract val systemsOrder: List<KClass<out EcsSystem>>

    protected lateinit var myCamera: MutableCamera


    @Before
    open fun setUp() {
        componentManager = EcsComponentManager()
        myCamera = MutableCamera(componentManager)

        liveMapContext = mock(LiveMapContext::class.java)

        val viewport = Viewport(ViewportHelper(WORLD_RECTANGLE, myLoopX = true, myLoopY = false), ClientPoint(500, 500), 1, 15)

        val mapRenderContext = mock(MapRenderContext::class.java)
        `when`(mapRenderContext.viewport).thenReturn(viewport)

        `when`(liveMapContext.mapRenderContext).thenReturn(mapRenderContext)
        `when`(liveMapContext.camera).thenReturn(myCamera)


        mySystemTime = mock(SystemTime::class.java)
        `when`(liveMapContext.systemTime).thenReturn(mySystemTime)

        addSystem<EcsSystem>(
            SchedulerSystem(
                MicroTaskCooperativeExecutor(liveMapContext, SCHEDULER_FRAME_TIME_LIMIT),
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

    protected fun update(specs: Iterable<MockSpec>) {
        deltaTimeSpec().standard().apply()
        schedulerSpec().runAll().apply()
        repeatSpec().times(1).apply()

        specs.forEach { it.apply() }

        while (updateRepeatTimes-- > 0) {
            systemsOrder.forEach { systemClass ->
                mySystems[systemClass]?.update(liveMapContext, dt)
            }
        }

        afterUpdateCleanup().forEach { it.apply() }
    }

    /**
     * By default, delta time is 16 and scheduler runs all microthreads
     */
    protected fun update(vararg specs: MockSpec) {
        update(listOf(*specs))
    }

    protected open fun afterUpdateCleanup(): List<MockSpec> {
        return emptyList()
    }

    protected fun <T : EcsSystem> addSystem(system: T): T {
        system.init(liveMapContext)
        mySystems[system::class] = system
        return system
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

    fun schedulerSpec(): SchedulerSpec {
        return SchedulerSpec(this)
    }

    private fun deltaTimeSpec(): DeltaTimeSpec {
        return DeltaTimeSpec(this)
    }

    internal fun repeatSpec(): RepeatSpec {
        return RepeatSpec(this)
    }

    protected fun assertEntityOrigin(name: String, p: WorldPoint) {
        assertTrue { getEntityComponent<WorldOriginComponent>(name).origin == p }
    }

    abstract class MockSpec protected constructor(internal val testBase: LiveMapTestBase) {

        protected val componentManager: EcsComponentManager
            get() = testBase.componentManager

        protected val liveMapContext: LiveMapContext
            get() = testBase.liveMapContext

        abstract fun apply()
    }

    protected class DeltaTimeSpec constructor(private val myTestBase: LiveMapTestBase) {

        internal fun standard(): MockSpec {
            return constant(16.0)
        }

        fun constant(dt: Double): MockSpec {
            return object : MockSpec(myTestBase) {
                override fun apply() {
                    testBase.dt = dt
                }
            }
        }
    }

    class SchedulerSpec internal constructor(private val myTestBase: LiveMapTestBase) {

        fun frameTime() : MockSpec {
            return object : MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(SCHEDULER_FRAME_TIME_LIMIT + 1L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun skipAll(): MockSpec {
            return object : MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(SCHEDULER_FRAME_TIME_LIMIT + 1L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun runAll(): MockSpec {
            return object : MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(0L)
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(0L)
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }

        fun runTimes(vararg times: Long): MockSpec {
            return object : MockSpec(myTestBase) {
                override fun apply() {
                    `when`(testBase.liveMapContext.frameStartTimeMs).thenReturn(times[0])
                    `when`(testBase.mySystemTime.getTimeMs()).thenReturn(times[0], *times.drop(1).toTypedArray())
                    `when`(testBase.liveMapContext.frameDurationMs).thenCallRealMethod()
                }
            }
        }
    }

    class RepeatSpec(private val myTestBase: LiveMapTestBase) {

        operator fun times(times: Int): MockSpec {
            return object : MockSpec(myTestBase) {
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