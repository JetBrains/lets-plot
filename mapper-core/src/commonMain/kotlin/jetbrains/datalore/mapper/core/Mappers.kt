/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.composite.Composites

object Mappers {
    fun isDescendant(ancestor: Mapper<*, *>, descendant: Mapper<*, *>): Boolean {
        return Composites.isDescendant(ancestor, descendant)
    }

    fun getRoot(mapper: Mapper<*, *>): Mapper<*, *> {
        return Composites.root(mapper)
    }

    fun attachRoot(mapper: Mapper<*, *>): Registration {
        mapper.attachRoot()
        return object : Registration() {
            override fun doRemove() {
                mapper.detachRoot()
            }
        }
    }

    fun <SourceT, Target1T, Target2T> compose(
            f1: MapperFactory<SourceT, Target1T>,
            f2: MapperFactory<Target1T, Target2T>
    ): MapperFactory<SourceT, Target2T> {
        return object : MapperFactory<SourceT, Target2T> {
            override fun createMapper(source: SourceT): Mapper<out SourceT, out Target2T> {
                val m1 = f1.createMapper(source)
                val m2 = f2.createMapper(m1.target)
                return object : Mapper<SourceT, Target2T>(m1.source, m2.target) {
                    private val children = createChildList<Mapper<*, *>>()

                    init {
                        children.add(m1)
                        children.add(m2)
                    }
                }
            }
        }
    }
}