/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.svg.shared

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode

interface TargetPeer<T> {
    fun appendChild(target: T, child: T)
    fun removeAllChildren(target: T)
    fun newSvgElement(source: SvgElement): T
    fun newSvgTextNode(source: SvgTextNode): T
    fun newSvgSlimNode(source: SvgSlimNode): T
    fun setAttribute(target: T, name: String, value: String)
    fun hookEventHandlers(source: SvgElement, target: T, eventSpecs: Set<SvgEventSpec>): Registration
}