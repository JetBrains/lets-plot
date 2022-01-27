/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.livemap.core.ecs.EcsComponent

/**
 * Entities with this component causes the map to show a spinner.
 * Have to be used to mark an important missing data - tiles, fragments, objects waiting for
 * geocoding and not rendering.
 */
class BusyStateComponent : EcsComponent {

}
