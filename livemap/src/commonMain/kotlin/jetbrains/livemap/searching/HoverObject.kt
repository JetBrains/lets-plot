/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

data class HoverObject(
    val layerIndex: Int,
    val index: Int,
    val distance: Double,
    val locator: Locator // TODO: move it out from HoverObject
)