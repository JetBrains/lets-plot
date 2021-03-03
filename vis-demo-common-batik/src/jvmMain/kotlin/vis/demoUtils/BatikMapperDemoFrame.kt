/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFrame
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import java.awt.Dimension
import javax.swing.JComponent


class BatikMapperDemoFrame(
    title: String,
    size: Dimension = FRAME_SIZE
) : SwingDemoFrame(title, size) {

    companion object {
        fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
            return BatikMapperComponent(
                svgRoot,
                BatikMapperComponent.DEF_MESSAGE_CALLBACK
            )
        }
    }
}