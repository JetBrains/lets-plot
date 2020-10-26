/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.vis.svg.event.SvgAttributeEvent

interface SvgNodeContainerListener {
    fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>)
    fun onNodeAttached(node: SvgNode)
    fun onNodeDetached(node: SvgNode)
}