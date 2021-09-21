/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.animation.Animation.Direction
import jetbrains.livemap.core.animation.Animation.Loop
import jetbrains.livemap.core.util.EasingFunction


class AnimationComponent : EcsComponent {
    var time: Double = 0.0
    var duration: Double = 0.0
    var finished: Boolean = false
    var progress: Double = 0.0
    lateinit var easingFunction: EasingFunction
    lateinit var loop: Loop
    lateinit var direction: Direction
}
