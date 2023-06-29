/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Value
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.observable.property.WritableProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SynchronizersTest {
    @Test
    fun forEventSourceOnAttach() {
        val runNum = Value(0)
        val prop = ValueProperty<Boolean?>(null)
        val synchronizer = Synchronizers.forEventSource(prop,
                object : Runnable {
                    override fun run() {
                        runNum.set(runNum.get() + 1)
                    }
                })

        val mapper = object : Mapper<Unit, Unit>(Unit, Unit) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                super.registerSynchronizers(conf)
                conf.add(synchronizer)
            }
        }
        mapper.attachRoot()

        assertEquals(1, runNum.get())
    }

    @Test
    fun forEventSourceHandler() {
        val prop = ValueProperty<Int?>(null)
        val handled = ArrayList<Int>()

        val mapper = object : Mapper<Unit, Unit>(Unit, Unit) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                super.registerSynchronizers(conf)
                conf.add(Synchronizers.forEventSource(prop) { item: PropertyChangeEvent<out Int?> ->
                    handled.add(item.newValue!!)
                })
            }
        }

        mapper.attachRoot()
        assertTrue(handled.isEmpty())

        prop.set(1)
        prop.set(2)
        prop.set(3)
        assertEquals(listOf(1, 2, 3), handled)

        mapper.detachRoot()
        prop.set(4)
        assertEquals(listOf(1, 2, 3), handled)
    }

    @Test
    fun forPropsOneWay() {
        val integerProperty = ValueProperty<Int?>(null)
        val numberProperty = ValueProperty<Number?>(null)

        val mapper = object : Mapper<Unit, Unit>(Unit, Unit) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                super.registerSynchronizers(conf)
                conf.add(Synchronizers.forPropsOneWay(integerProperty, numberProperty))
            }
        }
        mapper.attachRoot()

        integerProperty.set(1)
        assertEquals(1, numberProperty.get())
    }

    @Test
    fun forPropsOneWayRecorded() {
        val booleanProperty = ValueProperty<Boolean?>(null)
        val count = IntArray(1)

        val mapper = object : Mapper<Unit, Unit>(Unit, Unit) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                conf.add(Synchronizers.forPropsOneWay(booleanProperty, object : WritableProperty<Boolean?> {
                    override fun set(value: Boolean?) {
                        count[0]++
                    }
                }))
            }
        }
        mapper.attachRoot()


        // because null triggers sync as well
        assertEquals(1, count[0])

        booleanProperty.set(false)
        assertEquals(2, count[0])

        booleanProperty.set(false)
        assertEquals(2, count[0])

        booleanProperty.set(true)
        assertEquals(3, count[0])

        booleanProperty.set(false)
        assertEquals(4, count[0])
    }
}
