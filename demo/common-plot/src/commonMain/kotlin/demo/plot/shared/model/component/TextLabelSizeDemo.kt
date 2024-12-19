/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.component

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

open class TextLabelSizeDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(1200.0, 800.0)

        private fun createTextLabel(spec: LabelSpec): TextLabel {
            val label = TextLabel(spec.label)
            label.addClassName("label")
            label.setFontSize(spec.size)
            label.setFontWeight(spec.fontWeight ?: "normal")
            label.setFontStyle(spec.fontStyle ?: "normal")
            label.textColor().set(Color.DARK_BLUE)
            return label
        }
    }

    fun createModel(): GroupComponent {
        val specs = ArrayList<LabelSpec>()
        specs.add(LabelSpec(10.0))
        specs.add(LabelSpec(20.0))
        specs.add(LabelSpec(50.0))

        specs.add(LabelSpec(10.0, "bold"))
        specs.add(LabelSpec(20.0, "bold"))
        specs.add(LabelSpec(50.0, "bold"))

        specs.add(LabelSpec(10.0, "normal", "italic"))
        specs.add(LabelSpec(20.0, "normal", "italic"))
        specs.add(LabelSpec(50.0, "normal", "italic"))

        specs.add(LabelSpec(10.0, "bold", "italic"))
        specs.add(LabelSpec(20.0, "bold", "italic"))
        specs.add(LabelSpec(50.0, "bold", "italic"))

        val groupComponent = GroupComponent()
        val labelSize = DoubleVector(200.0, 50.0)
        var y = 50.0
        for (spec in specs) {
            val textLabel =
                createTextLabel(spec)
            val element = textLabel.rootGroup
            SvgUtils.transformTranslate(element, 100.0, y)
            groupComponent.add(element)

            y += labelSize.y
        }

        return groupComponent
    }

    private class LabelSpec(
            val size: Double,
            val fontWeight: String? = null,
            val fontStyle: String? = null) {

        val label: String = ("Label size=" + size
                + " weight: " + (fontWeight ?: "-")
                + " style: " + (fontStyle ?: "-"))
    }
}
