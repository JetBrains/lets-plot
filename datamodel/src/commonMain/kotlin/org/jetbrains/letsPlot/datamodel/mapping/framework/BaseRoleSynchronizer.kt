/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

abstract class BaseRoleSynchronizer<SourceT, TargetT> : RoleSynchronizer<SourceT, TargetT> {

    private var myMapperFactories: Array<MapperFactory<SourceT, TargetT>>? = null
    private var myErrorMapperFactories: Array<MapperFactory<SourceT, TargetT>>? = null
    private var myMapperProcessors: Array<MapperProcessor<SourceT, TargetT>>? = null

    private fun addMapperFactory(
        mapperFactories: Array<MapperFactory<SourceT, TargetT>>?,
        factory: MapperFactory<SourceT, TargetT>?):
            Array<MapperFactory<SourceT, TargetT>> {

        if (factory == null) {
            throw NullPointerException("mapper factory is null")
        }
//        val len = mapperFactories?.size ?: 0
//        val newMapperFactories = arrayOfNulls<MapperFactory<*, *>>(len + 1)
//        if (mapperFactories != null) {
//            System.arraycopy(mapperFactories, 0, newMapperFactories, 0, mapperFactories.size)
//        }
//        newMapperFactories[newMapperFactories.size - 1] = factory

        return if (mapperFactories == null) {
            arrayOf(factory)
        } else {
            Array(mapperFactories.size + 1) { i ->
                if (i < mapperFactories.size)
                    mapperFactories[i]
                else
                    factory
            }
        }
    }

    override fun addMapperFactory(factory: MapperFactory<SourceT, TargetT>) {
        myMapperFactories = addMapperFactory(myMapperFactories, factory)
    }

    override fun addErrorMapperFactory(factory: MapperFactory<SourceT, TargetT>) {
        myErrorMapperFactories = addMapperFactory(myErrorMapperFactories, factory)
    }

    override fun addMapperProcessor(processor: MapperProcessor<SourceT, TargetT>) {
//        val len = if (myMapperProcessors == null) 0 else myMapperProcessors!!.size
//
//        val newMapperProcessors = arrayOfNulls<MapperProcessor<*, *>>(len + 1)
//        if (myMapperProcessors != null) {
//            System.arraycopy(myMapperProcessors!!, 0, newMapperProcessors, 0, myMapperProcessors!!.size)
//        }
//        newMapperProcessors[newMapperProcessors.size - 1] = processor
//        myMapperProcessors = newMapperProcessors

        myMapperProcessors =
                if (myMapperProcessors == null) {
                    arrayOf(processor)
                } else {
                    Array(myMapperProcessors!!.size + 1) { i ->
                        if (i < myMapperProcessors!!.size)
                            myMapperProcessors!![i]
                        else
                            processor
                    }
                }
    }

    private fun createMapper(
        mapperFactories: Array<MapperFactory<SourceT, TargetT>>?,
        source: SourceT):
            Mapper<out SourceT, out TargetT>? {

        var result: Mapper<out SourceT, out TargetT>? = null
        if (mapperFactories != null) {
            for (f in mapperFactories) {
                result = f.createMapper(source)
                break
            }
        }
        return result
    }

    protected fun createMapper(source: SourceT): Mapper<out SourceT, out TargetT> {
        var result = createMapper(myMapperFactories, source)
        if (result == null) {
            result = createMapper(myErrorMapperFactories, source)
        }
        if (result == null) {
            throw IllegalStateException("Can't create a mapper for $source")
        }
        return result
    }

    fun processMapper(mapper: Mapper<out SourceT, out TargetT>) {
        if (myMapperProcessors != null) {
            for (p in myMapperProcessors!!) {
                p.process(mapper)
            }
        }
    }

}
