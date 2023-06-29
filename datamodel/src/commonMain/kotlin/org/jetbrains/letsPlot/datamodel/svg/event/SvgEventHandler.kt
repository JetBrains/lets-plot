/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.event

import jetbrains.datalore.base.event.Event
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

interface SvgEventHandler<EventT : Event> {
    fun handle(node: SvgNode, e: EventT)
}