/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import jetbrains.datalore.base.observable.collections.CollectionAdapter
import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.transform.Transformation
import org.jetbrains.letsPlot.datamodel.mapping.framework.transform.Transformer

internal class TransformingObservableCollectionRoleSynchronizer<SourceT, MappedT, TargetT>(
    mapper: Mapper<*, *>,
    private val mySource: SourceT,
    private val mySourceTransformer: Transformer<in SourceT, ObservableList<MappedT>>,
    private val myTarget: MutableList<in TargetT>,
    factory: MapperFactory<MappedT, TargetT>
) : BaseCollectionRoleSynchronizer<MappedT, TargetT>(mapper) {

    private var myCollectionRegistration: Registration? = null
    private var mySourceTransformation: Transformation<in SourceT, ObservableList<MappedT>>? = null

    init {

        addMapperFactory(factory)
    }

    override fun onAttach() {
        super.onAttach()
        val sourceList = ObservableArrayList<MappedT>()
        mySourceTransformation = mySourceTransformer.transform(mySource, sourceList)
        MapperUpdater().update(sourceList)
        for (m in modifiableMappers) {
            myTarget.add(m.target)
        }
        myCollectionRegistration = sourceList.addListener(object : CollectionAdapter<MappedT>() {
            override fun onItemAdded(event: CollectionItemEvent<out MappedT>) {
                val mapper = createMapper(event.newItem!!)
                modifiableMappers.add(event.index, mapper)
                myTarget.add(event.index, mapper.target)
                processMapper(mapper)
            }

            override fun onItemRemoved(event: CollectionItemEvent<out MappedT>) {
                modifiableMappers.removeAt(event.index)
                myTarget.removeAt(event.index)
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        myCollectionRegistration!!.remove()
        mySourceTransformation!!.dispose()
        myTarget.clear()
    }

}