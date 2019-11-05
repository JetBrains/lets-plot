/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.obj2entity.TextSpec

class TextComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}