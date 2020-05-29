/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind

class LiveMapLayerData(
    val geom: Geom,
    val geomKind: GeomKind,
    val aesthetics: Aesthetics
)
