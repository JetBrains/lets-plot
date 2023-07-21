/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.livemap.core.MetricsService

class EcsController(
    private val myComponentManager: EcsComponentManager,
    private val myContext: EcsContext,
    private val mySystems: List<EcsSystem>
) : Disposable {
    private val myDebugService: MetricsService = myContext.metricsService

    init {
        mySystems.forEach { it.init(myContext) }
    }

    fun update(dt: Double) {
        myContext.startFrame()

        myDebugService.reset()
        for (system in mySystems) {
            myDebugService.beginMeasureUpdate()
            system.update(myContext, dt)
            myDebugService.endMeasureUpdate(system)
        }

        myComponentManager.doRemove()
    }

    override fun dispose() {
        mySystems.forEach(EcsSystem::destroy)
    }
}