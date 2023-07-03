/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

abstract class BaseCollectionRoleSynchronizer<SourceT, TargetT>(mapper: Mapper<*, *>) : BaseRoleSynchronizer<SourceT, TargetT>() {

    val modifiableMappers: MutableList<Mapper<out SourceT, out TargetT>>

    private var myMappingContext: MappingContext? = null

    override val mappers: List<Mapper<out SourceT, out TargetT>>
        get() = modifiableMappers

    init {
        modifiableMappers = mapper.createChildList()
    }

    override fun attach(ctx: SynchronizerContext) {
        if (myMappingContext != null) {
            throw IllegalStateException()
        }

        myMappingContext = ctx.mappingContext

        onAttach()
    }

    override fun detach() {
        if (myMappingContext == null) {
            throw IllegalStateException()
        }

        onDetach()

        myMappingContext = null
    }

    protected open fun onAttach() {}

    protected open fun onDetach() {}

    protected open inner class MapperUpdater {
        fun update(sourceList: List<SourceT>) {
            val targetContent = ArrayList<SourceT>()
            val mappers = modifiableMappers
            for (m in mappers) {
                targetContent.add(m.source)
            }

            val difference = DifferenceBuilder(sourceList, targetContent).build()
            for (item in difference) {
                val itemIndex = item.index
                if (item.isAdd) {
                    val mapper = createMapper(item.item)
                    mappers.add(itemIndex, mapper)
                    mapperAdded(itemIndex, mapper)
                    processMapper(mapper)
                } else {
                    val mapper = mappers.removeAt(itemIndex)
                    mapperRemoved(itemIndex, mapper)
                }
            }
        }

        protected open fun mapperAdded(index: Int, mapper: Mapper<out SourceT, out TargetT>) {}

        protected open fun mapperRemoved(index: Int, mapper: Mapper<out SourceT, out TargetT>) {}
    }
}
