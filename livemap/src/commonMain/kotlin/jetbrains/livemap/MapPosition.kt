/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.projections.World

class MapPosition(val zoom: Int, val coordinate: Vec<World>)