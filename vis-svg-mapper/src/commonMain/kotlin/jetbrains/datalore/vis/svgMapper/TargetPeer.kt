/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svg.event.SvgEventSpec
import jetbrains.datalore.vis.svg.slim.SvgSlimNode

interface TargetPeer<T> {
    fun appendChild(target: T, child: T)
    fun removeAllChildren(target: T)
    fun newSvgElement(source: SvgElement): T
    fun newSvgTextNode(source: SvgTextNode): T
    fun newSvgSlimNode(source: SvgSlimNode): T
    fun setAttribute(target: T, name: String, value: String)
    fun hookEventHandlers(source: SvgElement, target: T, eventSpecs: Set<SvgEventSpec>): Registration
}