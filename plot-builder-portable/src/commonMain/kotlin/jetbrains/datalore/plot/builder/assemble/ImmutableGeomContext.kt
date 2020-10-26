/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector

interface ImmutableGeomContext : GeomContext {

    fun with(): Builder

    interface Builder {
        fun aesthetics(aesthetics: Aesthetics?): Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): Builder

        fun build(): ImmutableGeomContext
    }
}
