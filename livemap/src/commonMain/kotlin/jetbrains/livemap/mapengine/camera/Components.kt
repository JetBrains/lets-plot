/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.camera

import jetbrains.livemap.core.ecs.EcsComponent

class ZoomFractionChangedComponent : EcsComponent

// Zoom changed to a value and fraction part is zero, e.g. 1.4 -> 2.0, or 1.3 -> 1.0
class ZoomLevelChangedComponent : EcsComponent

class CenterChangedComponent : EcsComponent

class CameraListenerComponent : EcsComponent

class CameraComponent(private val myCamera: Camera) : EcsComponent {
    val zoom get() = myCamera.zoom
    val position get() = myCamera.position
}