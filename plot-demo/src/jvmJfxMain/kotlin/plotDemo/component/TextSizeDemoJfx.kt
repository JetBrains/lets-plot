/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plotDemo.model.component.TextSizeEstimationDemo
import jetbrains.datalore.vis.demoUtils.swing.TextSizeDemoWindow
import jetbrains.datalore.vis.demoUtils.swing.TextSettings
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.SwingUtilities

fun main() {
    fun buildPlotComponent(demoInnerSize: Dimension, settings: TextSettings): JComponent? {
        fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
            val component = SceneMapperJfxPanel(
                svgRoot,
                stylesheets = listOf(Style.JFX_PLOT_STYLESHEET)
            )
            component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
            return component
        }

        val svgRoot = TextSizeEstimationDemo.createSvgElement(
            DoubleVector(demoInnerSize.width.toDouble(), demoInnerSize.height.toDouble()),
            settings.lines,
            settings.fontName,
            settings.fontSize,
            settings.isBold,
            settings.isItalic,
            settings.isMonospaced,
            settings.fontWidthRatio
        )
        return svgRoot?.let(::createPlotComponent)
    }

    SwingUtilities.invokeLater {
        TextSizeDemoWindow(
            "Text size estimation",
            Dimension(1200, 1000),
            ::buildPlotComponent,
            categoryNames = TextSizeEstimationDemo.CharCategory.getCharCategoryNamesWithRatios(),
            categoryToChars = TextSizeEstimationDemo.CharCategory::getCharsForCategory
        ).run()
    }
}