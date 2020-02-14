/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg.slim

import jetbrains.datalore.vis.svg.SvgNode

interface SvgSlimGroup : SvgSlimObject {
    //  void addChild(SvgSlimObject child);
    fun asDummySvgNode(): SvgNode
}
