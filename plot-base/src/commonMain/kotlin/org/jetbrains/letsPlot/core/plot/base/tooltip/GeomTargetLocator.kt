/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomKind

interface GeomTargetLocator {

    fun search(coord: DoubleVector): LookupResult?

    enum class LookupSpace {
        X, Y, XY, NONE;

        fun isUnivariate() = this === X || this === Y
    }

    enum class LookupStrategy {
        HOVER, NEAREST, NONE
    }

    class LookupSpec(val lookupSpace: LookupSpace, val lookupStrategy: LookupStrategy) {
        companion object {
            val NONE = LookupSpec(
                LookupSpace.NONE,
                LookupStrategy.NONE
            )
        }
    }

    // `open` for Mockito test
    data class LookupResult(
        val targets: List<GeomTarget>,
        val distance: Double,
        val geomKind: GeomKind,
        val contextualMapping: ContextualMapping,
        val hitShapeKind: HitShape.Kind,
    ) {
        val hasGeneralTooltip: Boolean = contextualMapping.hasGeneralTooltip
        val hasAxisTooltip: Boolean = contextualMapping.hasAxisTooltip
        val isCrosshairEnabled: Boolean = contextualMapping.isCrosshairEnabled
        val tooltipGroup: String? = contextualMapping.tooltipGroup
    }

    object NullGeomTargetLocator : GeomTargetLocator {
        override fun search(coord: DoubleVector): LookupResult? = null
    }
}
