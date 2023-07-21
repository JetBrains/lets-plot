/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.Mocks
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroThreadComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import kotlin.test.assertTrue

class SchedulerSystemTest : org.jetbrains.letsPlot.livemap.LiveMapTestBase() {

    override val systemsOrder= listOf(SchedulerSystem::class)

    override fun setUp() {
        super.setUp()

        @Suppress("UNCHECKED_CAST")
        val microThread = Mockito.mock(MicroTask::class.java) as MicroTask<Unit>
        createEntity(SINGLE_IT, MicroThreadComponent(microThread, 1))
    }

    @Test
    fun simple() {

        update(
            microThread(this).alive(SINGLE_IT),
            Mocks.scheduler(this).frameTime()
        )

        assertTrue { getEntity(SINGLE_IT).contains<MicroThreadComponent>() }

        update(
            microThread(this).finished(SINGLE_IT),
            Mocks.scheduler(this).runAll()
        )

        assertTrue { !getEntity(SINGLE_IT).contains<MicroThreadComponent>() }
    }

    fun microThread(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) = MicroThreadSpec(testBase)

    class MicroThreadSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
        lateinit var myEntityName: String
        var myAlive: Boolean = true


        override fun apply() {
            testBase.getEntity(myEntityName).get<MicroThreadComponent>().run {
                `when`(microTask.alive()).thenReturn(myAlive)
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