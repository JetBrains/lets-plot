/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity

object ComponentManagerUtil {
    fun getEntity(name: String, componentManager: EcsComponentManager): EcsEntity? {
        return componentManager.entities.firstOrNull() { entity -> entity.name == name }
    }
}