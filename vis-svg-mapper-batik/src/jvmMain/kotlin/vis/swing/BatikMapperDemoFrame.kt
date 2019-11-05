/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFrame
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import javax.swing.JComponent


class BatikMapperDemoFrame(
    title: String,
    size: Dimension = FRAME_SIZE
) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
        return Companion.createSvgComponent(svgRoot)
    }

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, size: DoubleVector, title: String) {
            BatikMapperDemoFrame(title).showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement): JComponent {
            return BatikMapperComponent(
                svgRoot,
                BatikMapperComponent.DEF_MESSAGE_CALLBACK
            )
        }
    }
}