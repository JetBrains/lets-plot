/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.observable.event.SimpleEventSource
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl

abstract class BaseLiveMap : EventSource<Throwable>, Disposable {
    private val throwableSource = SimpleEventSource<Throwable>()
    val isLoading: Property<Boolean> = ValueProperty(true)

    abstract fun draw(canvasControl: CanvasControl)

    override fun addHandler(handler: EventHandler<in Throwable>): Registration {
        return throwableSource.addHandler(handler)
    }

    protected fun fireThrowable(throwable: Throwable) {
        throwableSource.fire(throwable)
    }
}