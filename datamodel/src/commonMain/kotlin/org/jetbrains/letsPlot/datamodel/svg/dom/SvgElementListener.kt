/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent

interface SvgElementListener {
    fun onAttrSet(event: SvgAttributeEvent<*>)
}