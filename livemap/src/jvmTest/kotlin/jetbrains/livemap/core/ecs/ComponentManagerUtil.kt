/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.core.ecs

import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity

object ComponentManagerUtil {
    fun getEntity(name: String, componentManager: EcsComponentManager): EcsEntity? {
        return componentManager.entities.filter { entity -> entity.name == name }[0]
    }
}