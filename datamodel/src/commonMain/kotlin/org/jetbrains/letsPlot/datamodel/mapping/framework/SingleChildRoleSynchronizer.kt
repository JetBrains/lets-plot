/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.registration.Registration

internal class SingleChildRoleSynchronizer<SourceT, TargetT>(
    mapper: Mapper<*, *>,
    private val myChildProperty: ReadableProperty<out SourceT?>,
    private val myTargetProperty: WritableProperty<in TargetT?>,
    factory: MapperFactory<SourceT, TargetT>
) :

        BaseRoleSynchronizer<SourceT, TargetT>() {

    private val myTargetMapper: Property<Mapper<out SourceT, out TargetT>?> = mapper.createChildProperty()
    private var myChildRegistration: Registration = Registration.EMPTY

    override val mappers: List<Mapper<out SourceT, out TargetT>>
        get() = if (myTargetMapper.get() == null) {
            emptyList()
        } else listOf(myTargetMapper.get()!!)


    init {
        addMapperFactory(factory)
    }


    override fun attach(ctx: SynchronizerContext) {
        sync()
        myChildRegistration = myChildProperty.addHandler(object : EventHandler<PropertyChangeEvent<out SourceT?>> {
            override fun onEvent(event: PropertyChangeEvent<out SourceT?>) {
                sync()
            }
        })
    }

    override fun detach() {
        myChildRegistration.remove()
        myTargetProperty.set(null)
        myTargetMapper.set(null)
    }

    private fun sync() {
        val modelValue = myChildProperty.get()
//        val viewValue = if (myTargetMapper.get() == null) null else myTargetMapper.get().source
        val viewValue = myTargetMapper.get()?.source
        if (modelValue === viewValue) return

        if (modelValue != null) {
            val mapper = createMapper(modelValue)
            myTargetMapper.set(mapper)
            myTargetProperty.set(mapper.target)
            processMapper(mapper)
        } else {
            myTargetMapper.set(null)
            myTargetProperty.set(null)
        }
    }

}