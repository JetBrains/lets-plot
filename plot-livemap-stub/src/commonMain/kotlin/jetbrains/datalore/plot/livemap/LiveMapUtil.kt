/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.builder.GeomLayer

object LiveMapUtil {
    fun injectLiveMapProvider(
        @Suppress("UNUSED_PARAMETER") plotTiles: List<List<GeomLayer>>,
        @Suppress("UNUSED_PARAMETER") liveMapOptions: LiveMapOptions
    ) {
        // do nothing
    }
}