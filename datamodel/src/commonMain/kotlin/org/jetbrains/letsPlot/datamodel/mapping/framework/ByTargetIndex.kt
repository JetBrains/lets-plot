/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * Index which allows efficient lookup of mappers in specified mapping context by their target
 */
class ByTargetIndex(ctx: MappingContext) : Disposable {

    private val myRegistration: Registration
    private val myTargetToMappers = HashMap<Any, MutableSet<Mapper<*, *>>>()

    init {
        for (mapper in ctx.getMappers()) {
            if (mapper.isFindable) {
                putMapper(mapper)
            }
        }

        myRegistration = ctx.addListener(object : MappingContextListener {
            override fun onMapperRegistered(mapper: Mapper<*, *>) {
                if (!mapper.isFindable) return

                putMapper(mapper)
            }

            override fun onMapperUnregistered(mapper: Mapper<*, *>) {
                if (!mapper.isFindable) return

                val target = mapper.target!!
                val mappers = getMappers(target)
                if (!mappers.contains(mapper)) {
                    throw IllegalStateException("unregistered mapper $mapper with target $target")
                }
                mappers.remove(mapper)
            }
        })
    }

    private fun putMapper(mapper: Mapper<*, *>) {
        val target = mapper.target!!
        val mapperSet = getMappers(target)
        mapperSet.add(mapper)
    }

    fun getMappers(target: Any): MutableSet<Mapper<*, *>> {
        val result: MutableSet<Mapper<*, *>>
        val mappers = myTargetToMappers[target]
        if (mappers == null) {
            result = HashSet()
            myTargetToMappers[target] = result
        } else {
            result = mappers
        }
        return result
    }

    override fun dispose() {
        myRegistration.remove()
    }

    companion object {

        val KEY = MappingContextProperty<ByTargetIndex>("ByTargetIndex")
    }
}
