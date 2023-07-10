/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry

interface ImmutableGeomContext : GeomContext {

    fun with(): Builder

    interface Builder {
        fun flipped(flipped: Boolean): Builder

        fun aesthetics(aesthetics: Aesthetics): Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, ScaleMapper<*>>): Builder

        fun aesBounds(aesBounds: DoubleRectangle): Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): Builder

        fun fontFamilyRegistry(v: FontFamilyRegistry): Builder

        fun annotations(annotations: Annotations?): Builder

        fun build(): ImmutableGeomContext
    }
}
