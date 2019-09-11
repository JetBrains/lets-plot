package jetbrains.datalore.jetbrains.livemap.core.multitasking

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.core.multitasking.MicroThreadComponent
import jetbrains.livemap.core.multitasking.SchedulerSystem
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import kotlin.test.assertTrue

class SchedulerSystemTest : LiveMapTestBase() {

    override val systemsOrder= listOf(SchedulerSystem::class)

    override fun setUp() {
        super.setUp()

        val microThread = Mockito.mock(MicroTask::class.java) as MicroTask<Unit>
        createEntity(SINGLE_IT, MicroThreadComponent(microThread, 1))
    }

    @Test
    fun simple() {

        update(
            microThread(this).alive(SINGLE_IT),
            Mocks.scheduler(this).frameTime()
        )

        assertTrue { getEntity(SINGLE_IT).contains(MicroThreadComponent::class) }

        update(
            microThread(this).finished(SINGLE_IT),
            Mocks.scheduler(this).runAll()
        )

        assertTrue { !getEntity(SINGLE_IT).contains(MicroThreadComponent::class) }
    }

    fun microThread(testBase: LiveMapTestBase) = MicroThreadSpec(testBase)

    class MicroThreadSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {
        lateinit var myEntityName: String
        var myAlive: Boolean = true


        override fun apply() {
            testBase.getEntity(myEntityName).get<MicroThreadComponent>().run {
                `when`(microThread.alive()).thenReturn(myAlive)
            }
        }

        fun alive(entityName: String): MicroThreadSpec {
            myEntityName = entityName
            myAlive = true
            return this
        }

        fun finished(entityName: String): MockSpec {
            myEntityName = entityName
            myAlive = false
            return this
        }
    }

    companion object {
        const val SINGLE_IT = "single_it"
    }
}