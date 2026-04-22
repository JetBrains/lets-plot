/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.GeomKind

// `open` for Mockito test
data class LookupResult(
    val targets: List<GeomTarget>,
    // Locator distance for the whole matched result.
    // Examples:
    // POINT + NEAREST + XY -> XY distance to the point.
    // PATH + HOVER + X -> XY distance to nearest path point.
    // RECT + HOVER + X -> 0.0 when cursor is inside the X range.
    val lookupDistance: Double,
    // Distance to the nearest tooltip owner in this result, measured in lookup space.
    // Examples:
    // POINT + NEAREST + XY -> XY distance to the point.
    // PATH + HOVER + X -> abs(dx) to nearest tooltip owner.
    // RECT + HOVER + X -> abs(dx) to the rect tooltip anchor.
    val ownerDistance: Double,
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
