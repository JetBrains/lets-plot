/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.vis.svg.event.SvgAttributeEvent

open class SvgNodeContainerAdapter : SvgNodeContainerListener {
    override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {}

    override fun onNodeAttached(node: SvgNode) {}

    override fun onNodeDetached(node: SvgNode) {}
}