/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.scaling

import jetbrains.livemap.core.ecs.EcsComponent

class ScaleComponent : EcsComponent {
    var scale = 1.0
    var zoom: Int = 0
}