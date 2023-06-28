/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

interface SvgRoot {
    fun add(node: SvgNode)
}
