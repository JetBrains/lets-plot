/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.livemap.core.ecs.EcsComponent

class IndexComponent(val index: Int): EcsComponent

class LocatorComponent(val locatorHelper: LocatorHelper): EcsComponent

val SEARCH_COMPONENTS = listOf(
    IndexComponent::class,
    LocatorComponent::class
)