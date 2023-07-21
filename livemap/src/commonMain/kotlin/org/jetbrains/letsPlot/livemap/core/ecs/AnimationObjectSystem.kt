/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs


class AnimationObjectSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {
    override fun init(context: EcsContext) {}

    override fun update(context: EcsContext, dt: Double) {
        getEntities(AnimationObjectComponent::class).forEach {

            with (it.getComponent<AnimationObjectComponent>().animation) {
                time += dt
                animate()
                if (isFinished) {
                    it.removeComponent(AnimationObjectComponent::class)
                }
            }
        }
    }
}
