/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.component.TextSizeEstimationDemo
import jetbrains.datalore.vis.demoUtils.swing.TextSettings
import jetbrains.datalore.vis.demoUtils.swing.TextSizeDemoCharCategories
import jetbrains.datalore.vis.demoUtils.swing.TextSizeDemoWindow
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.SwingUtilities

fun main() {
    fun buildPlotComponent(demoInnerSize: Dimension, settings: TextSettings): JComponent? {
        fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
            val component = BatikMapperComponent(svgRoot, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
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
            settings.fontWidthRatio,
            settings.categoryRatio,
            settings.boldRatio
        )
        return svgRoot?.let(::createPlotComponent)
    }

    SwingUtilities.invokeLater {
        TextSizeDemoWindow(
            "Text size estimation",
            Dimension(1600, 1200),
            ::buildPlotComponent,
            categoryNames = TextSizeDemoCharCategories.getCategoryNamesForDemo(),
            categoryToChars = TextSizeDemoCharCategories::getCharsForCategory,
            fontToDefaultRatio = TextSizeDemoCharCategories::getFontRatio,
            categoryToDefaultRatio = TextSizeDemoCharCategories::getDefaultRatioForCategory
        ).run()
    }
}