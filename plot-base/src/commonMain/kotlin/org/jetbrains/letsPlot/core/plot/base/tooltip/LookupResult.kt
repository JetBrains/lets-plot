/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.GeomKind

// `open` for Mockito test
data class LookupResult(
    val targets: List<GeomTarget>,
    val lookupDistance: Double,
    val lookupSpec: GeomTargetLocator.LookupSpec,
    val geomKind: GeomKind,
    val contextualMapping: ContextualMapping,
    val hitShapeKind: HitShape.Kind
) {
    val hasGeneralTooltip: Boolean = contextualMapping.hasGeneralTooltip
    val hasAxisTooltip: Boolean = contextualMapping.hasAxisTooltip
    val isCrosshairEnabled: Boolean = contextualMapping.isCrosshairEnabled
    val tooltipGroup: String? = contextualMapping.tooltipGroup
}