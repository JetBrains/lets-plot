/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

open class MapObject(
    val index: Int,
    var mapId: String?,
    regionId: String?
) {
    var regionId = regionId
        set(value) {
            require(value?.let {it.toIntOrNull() != null} ?: true) { "regionId should be a number" }
            field = value
        }
}