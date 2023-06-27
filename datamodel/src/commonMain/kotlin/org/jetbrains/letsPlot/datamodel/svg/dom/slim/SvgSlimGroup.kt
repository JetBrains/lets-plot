/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

interface SvgSlimGroup : SvgSlimObject {
    //  void addChild(SvgSlimObject child);
    fun asDummySvgNode(): SvgNode
}
