/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import jetbrains.datalore.base.registration.Registration

internal class ObservableCollectionRoleSynchronizer<SourceT, TargetT>(
    mapper: Mapper<*, *>,
    private val mySource: ObservableList<out SourceT>,
    private val myTarget: MutableList<in TargetT>,
    factory: MapperFactory<SourceT, TargetT>,
    errorMapperFactory: MapperFactory<SourceT, TargetT>?) :
        BaseCollectionRoleSynchronizer<SourceT, TargetT>(mapper) {
    private var myCollectionRegistration: Registration? = null

    init {

        addMapperFactory(factory)
        if (errorMapperFactory != null) {
            addErrorMapperFactory(errorMapperFactory)
        }
    }

    override fun onAttach() {
        super.onAttach()

        if (!myTarget.isEmpty()) {
            throw IllegalArgumentException("Target Collection Should Be Empty")
        }

        myCollectionRegistration = Registration.EMPTY

        MapperUpdater().update(mySource)
        val modifiableMappers = modifiableMappers
        for (m in modifiableMappers) {
            myTarget.add(m.target)
        }

        myCollectionRegistration = mySource.addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<SourceT>() {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out SourceT>) {
                val mapper = createMapper(event.newItem!!)
                modifiableMappers.add(event.index, mapper)
                myTarget.add(event.index, mapper.target)
                processMapper(mapper)
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out SourceT>) {
                modifiableMappers.removeAt(event.index)
                myTarget.removeAt(event.index)
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        myCollectionRegistration!!.remove()
        myTarget.clear()
    }
}