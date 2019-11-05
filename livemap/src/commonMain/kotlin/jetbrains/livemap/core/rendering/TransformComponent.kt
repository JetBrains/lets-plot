/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent

class TransformComponent : EcsComponent {
    var scale: Double = 0.0
    var position: DoubleVector = DoubleVector.ZERO
}