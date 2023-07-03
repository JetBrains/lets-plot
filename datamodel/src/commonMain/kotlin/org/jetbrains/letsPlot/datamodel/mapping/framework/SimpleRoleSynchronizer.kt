/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

/**
 * Simple collection synchronizer.
 * Synchronizes two non observable collections by invoking refresh() method.
 */
class SimpleRoleSynchronizer<SourceT, TargetT>
internal constructor(
    mapper: Mapper<*, *>,
    private val mySource: List<SourceT>,
    private val myTarget: MutableList<TargetT>,
    factory: MapperFactory<SourceT, TargetT>
) :

        BaseCollectionRoleSynchronizer<SourceT, TargetT>(mapper), RefreshableSynchronizer {

    init {
        addMapperFactory(factory)
    }

    override fun refresh() {
        object : MapperUpdater() {
            override fun mapperAdded(index: Int, mapper: Mapper<out SourceT, out TargetT>) {
                myTarget.add(index, mapper.target)
            }

            override fun mapperRemoved(index: Int, mapper: Mapper<out SourceT, out TargetT>) {
                myTarget.removeAt(index)
            }
        }.update(mySource)
    }

    override fun onAttach() {
        super.onAttach()
        refresh()
    }

    override fun onDetach() {
        super.onDetach()
        myTarget.clear()
    }
}