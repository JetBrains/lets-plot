/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.Annotation
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

interface ImmutableGeomContext : GeomContext {

    fun with(): Builder

    interface Builder {
        fun flipped(flipped: Boolean): Builder

        fun aesthetics(aesthetics: Aesthetics): Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, ScaleMapper<*>>): Builder

        fun aesBounds(aesBounds: DoubleRectangle): Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): Builder

        fun fontFamilyRegistry(v: FontFamilyRegistry): Builder

        fun annotation(annotation: Annotation?): Builder

        fun defaultFormatters(defaultFormatters: Map<Any, (Any) -> String>): Builder

        fun backgroundColor(color: Color): Builder

        fun plotContext(plotContext: PlotContext): Builder

        fun coordinateSystem(coordinateSystem: CoordinateSystem): Builder

        fun build(): ImmutableGeomContext
    }
}
