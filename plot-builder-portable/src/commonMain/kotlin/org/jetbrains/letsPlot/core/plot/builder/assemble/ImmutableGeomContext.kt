/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry

interface ImmutableGeomContext : GeomContext {

    fun with(): Builder

    interface Builder {
        fun flipped(flipped: Boolean): Builder

        fun aesthetics(aesthetics: Aesthetics): Builder

        fun aestheticMappers(aestheticMappers: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>): Builder

        fun aesBounds(aesBounds: DoubleRectangle): Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): Builder

        fun fontFamilyRegistry(v: FontFamilyRegistry): Builder

        fun annotations(annotations: Annotations?): Builder

        fun build(): ImmutableGeomContext
    }
}
