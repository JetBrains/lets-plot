/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projections.WorldPoint

class ZoomChangedComponent : EcsComponent

class CenterChangedComponent : EcsComponent

class CameraListenerComponent : EcsComponent

class CameraUpdateComponent : EcsComponent {

    var isZoomChanged: Boolean = false
    var isMoved: Boolean = false

    fun nothing(): CameraUpdateComponent {
        isMoved = false
        isZoomChanged = false
        return this
    }
}

class CameraComponent(var zoom: Double, var position: WorldPoint) : EcsComponent

class UpdateViewportComponent : EcsComponent