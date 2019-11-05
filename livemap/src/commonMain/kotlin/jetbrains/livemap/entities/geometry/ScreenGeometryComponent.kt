/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent


class ScreenGeometryComponent : EcsComponent {
    lateinit var geometry: ClientGeometry
    var zoom: Int = 0
}
