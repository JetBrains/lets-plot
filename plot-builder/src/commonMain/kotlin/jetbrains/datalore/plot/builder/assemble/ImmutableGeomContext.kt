/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector

interface ImmutableGeomContext : GeomContext {

    fun with(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

    interface Builder {
        fun aesthetics(aesthetics: Aesthetics?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun build(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext
    }
}
