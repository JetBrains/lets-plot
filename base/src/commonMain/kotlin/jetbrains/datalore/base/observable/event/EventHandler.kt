/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.event

/**
 * Handler for events fired by [EventSource]
 */
interface EventHandler<in EventT> {
    fun onEvent(event: EventT)
}