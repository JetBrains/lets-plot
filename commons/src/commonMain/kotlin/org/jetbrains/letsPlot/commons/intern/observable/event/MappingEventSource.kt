/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event

import jetbrains.datalore.base.registration.Registration

internal class MappingEventSource<SourceEventT, TargetEventT>(
    private val mySourceEventSource: EventSource<SourceEventT>,
    private val myFunction: (SourceEventT) -> TargetEventT
) : EventSource<TargetEventT> {

    override fun addHandler(handler: EventHandler<TargetEventT>): Registration {
        return mySourceEventSource.addHandler(object : EventHandler<SourceEventT> {
            override fun onEvent(event: SourceEventT) {
                handler.onEvent(myFunction(event))
            }
        })
    }
}