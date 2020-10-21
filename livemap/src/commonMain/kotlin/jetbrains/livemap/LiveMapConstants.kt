/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.spatial.GeoRectangle

object LiveMapConstants {
    const val MIN_ZOOM = 1
    const val MAX_ZOOM = 15
    val DEFAULT_LOCATION = GeoRectangle(-124.76, 25.52, -66.94, 49.39)
    const val TILE_PIXEL_SIZE = 256.0
}


