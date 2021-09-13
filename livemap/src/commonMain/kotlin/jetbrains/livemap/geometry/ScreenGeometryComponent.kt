/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geometry

import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsComponent


class ScreenGeometryComponent : EcsComponent {
    lateinit var geometry: MultiPolygon<Client>
    var zoom: Int = 0
}
