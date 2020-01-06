/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.projections

enum class ProjectionType {
    GEOGRAPHIC,
    MERCATOR,
    AZIMUTHAL_EQUAL_AREA,
    AZIMUTHAL_EQUIDISTANT,
    CONIC_CONFORMAL,
    CONIC_EQUAL_AREA
}