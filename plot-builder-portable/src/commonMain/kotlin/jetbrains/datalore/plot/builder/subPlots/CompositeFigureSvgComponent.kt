/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.subPlots

import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.FigureSvgRoot

class CompositeFigureSvgComponent(
    val elements: List<FigureSvgRoot>,
) : SvgComponent() {

    override fun buildComponent() {
        // ToDo: add title, subtitle, caption
    }

    override fun clear() {
        super.clear()
    }
}