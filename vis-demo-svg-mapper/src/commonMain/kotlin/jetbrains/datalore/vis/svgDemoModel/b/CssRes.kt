/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgDemoModel.b

import jetbrains.datalore.vis.svg.SvgCssResource

class CssRes : SvgCssResource {
    override fun css(): String {
        return ".ellipse-yellow { \n" +
                "fill: yellow;\n" +
                "}"
    }
}