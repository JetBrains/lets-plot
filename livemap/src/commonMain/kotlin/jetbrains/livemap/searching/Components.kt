/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsComponent

class IndexComponent(val layerIndex: Int, val index: Int): EcsComponent

class LocatorComponent(val locator: LocatorHelper): EcsComponent

class SearchResultComponent : EcsComponent {
    var hoverObjects: List<HoverObject> = emptyList()
    var zoom : Int? = null
    var cursorPosition : Vec<Client>? = null
}

val SEARCH_COMPONENTS = listOf(
    LocatorComponent::class,
    IndexComponent::class
)