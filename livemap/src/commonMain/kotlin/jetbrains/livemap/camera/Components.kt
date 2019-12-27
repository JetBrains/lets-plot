/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.livemap.core.ecs.EcsComponent

class ZoomChangedComponent : EcsComponent

class CenterChangedComponent : EcsComponent

class CameraListenerComponent : EcsComponent

class CameraComponent(private val myCamera: Camera) : EcsComponent {
    val zoom get() = myCamera.zoom
    val position get() = myCamera.position
}