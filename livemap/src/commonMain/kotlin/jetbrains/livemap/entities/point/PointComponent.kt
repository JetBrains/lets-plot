/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.maps.livemap.entities.point

import jetbrains.livemap.core.ecs.EcsComponent


class PointComponent : EcsComponent {
    var shape: Int = 0
}
