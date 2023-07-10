/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event

import jetbrains.datalore.base.registration.Registration

/**
 * Source of events of type EventT
 */
interface EventSource<EventT> {
    fun addHandler(handler: EventHandler<EventT>): Registration
}