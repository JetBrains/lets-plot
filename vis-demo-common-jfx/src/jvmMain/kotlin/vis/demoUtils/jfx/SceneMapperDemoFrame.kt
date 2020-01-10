/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.jfx

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.demoUtils.swing.SwingDemoFrame
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import java.awt.Dimension
import javax.swing.JComponent

class SceneMapperDemoFrame(
    title: String,
    private val stylesheets: List<String>,
    size: Dimension = FRAME_SIZE
) : SwingDemoFrame(title, size) {

    override fun createSvgComponent(svgRoot: SvgSvgElement): JComponent =
        createSvgComponent(
            svgRoot,
            stylesheets
        )

    companion object {
        fun showSvg(svgRoots: List<SvgSvgElement>, stylesheets: List<String>, size: DoubleVector, title: String) {
            SceneMapperDemoFrame(title, stylesheets)
                .showSvg(svgRoots, size)
        }

        fun createSvgComponent(svgRoot: SvgSvgElement, stylesheets: List<String>) =
            SceneMapperJfxPanel(svgRoot, stylesheets)
    }
}