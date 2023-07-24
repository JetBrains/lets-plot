/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.js.component

import demo.plot.shared.model.component.AxisComponentDemo
import org.jetbrains.letsPlot.platf.w3c.mapping.util.SvgToW3c.generateDom

/**
 * Called from generated HTML
 * Run with AxisComponentDemoBrowser.kt
 */
fun axisComponentDemo() {
    with(AxisComponentDemo()) {
        val svgRoots = createSvgRoots()
        generateDom(svgRoots, "root")
    }
}

