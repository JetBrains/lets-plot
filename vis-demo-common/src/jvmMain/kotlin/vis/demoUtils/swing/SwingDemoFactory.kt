/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent

interface SwingDemoFactory {
    fun createDemoFrame(title: String,
                        size: Dimension = SwingDemoFrame.FRAME_SIZE
    ): SwingDemoFrame

    fun createSvgComponent(svg: SvgSvgElement): JComponent

    fun createPlotEdtExecutor(): (() -> Unit) -> Unit
}

