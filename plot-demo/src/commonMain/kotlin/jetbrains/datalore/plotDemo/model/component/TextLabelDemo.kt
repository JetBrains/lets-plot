/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor.*
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor.*
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import org.jetbrains.letsPlot.datamodel.svg.dom.*

open class TextLabelDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    override val cssStyle: String
        get() = ".$LABEL_CLASS_NAME { font-size: 18px; }"

    fun createModel(): GroupComponent {
        val specs = ArrayList<LabelSpec>()
        specs.add(LabelSpec(LEFT, BOTTOM, 0.0))
        specs.add(LabelSpec(MIDDLE, BOTTOM, 0.0))
        specs.add(LabelSpec(RIGHT, BOTTOM, 0.0))
        specs.add(LabelSpec(LEFT, CENTER, 0.0))
        specs.add(LabelSpec(MIDDLE, CENTER, 0.0))
        specs.add(LabelSpec(RIGHT, CENTER, 0.0))
        specs.add(LabelSpec(LEFT, TOP, 0.0))
        specs.add(LabelSpec(MIDDLE, TOP, 0.0))
        specs.add(LabelSpec(RIGHT, TOP, 0.0))

        specs.add(LabelSpec(LEFT, BOTTOM, 30.0))
        specs.add(LabelSpec(MIDDLE, BOTTOM, 30.0))
        specs.add(LabelSpec(RIGHT, BOTTOM, 30.0))
        specs.add(LabelSpec(LEFT, CENTER, 30.0))
        specs.add(LabelSpec(MIDDLE, CENTER, 30.0))
        specs.add(LabelSpec(RIGHT, CENTER, 30.0))
        specs.add(LabelSpec(LEFT, TOP, 30.0))
        specs.add(LabelSpec(MIDDLE, TOP, 30.0))
        specs.add(LabelSpec(RIGHT, TOP, 30.0))

        val groupComponent = GroupComponent()

        var exampleDim = DoubleVector(200.0, 50.0)
        var y = 50
        for ((i, spec) in specs.withIndex()) {
            if (i == 9) {
                exampleDim = exampleDim.add(DoubleVector(0.0, 50.0))
            }
            val labelExample = createLabelExample(
                exampleDim,
                spec.hAnchor,
                spec.vAnchor,
                spec.angle
            )
            SvgUtils.transformTranslate(labelExample, 300.0, y.toDouble())
            groupComponent.add(labelExample)
            y += exampleDim.y.toInt()
        }

        return groupComponent
    }

    private class LabelSpec(
        val hAnchor: HorizontalAnchor,
        val vAnchor: VerticalAnchor,
        val angle: Double
    )


    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(800.0, 1500.0)
        private const val LABEL_CLASS_NAME = "label"

        private fun createLabelExample(
            dim: DoubleVector,
            hAnchor: HorizontalAnchor,
            vAnchor: VerticalAnchor,
            angle: Double
        ): SvgGElement {
            val axis = createAxis(dim)
            val textLabel = createTextLabel(
                hAnchor,
                vAnchor,
                angle
            )
            textLabel.moveTo(dim.x / 2, dim.y / 2)

            val g = SvgGElement()
            g.children().add(axis)
            g.children().add(textLabel.rootGroup)
            return g
        }

        private fun createAxis(dim: DoubleVector): SvgElement {
            val hAxis = SvgLineElement(0.0, dim.y / 2, dim.x, dim.y / 2)
            hAxis.stroke().set(SvgColors.RED)
            val vAxis = SvgLineElement(dim.x / 2, 0.0, dim.x / 2, dim.y)
            vAxis.stroke().set(SvgColors.RED)
            val c = SvgCircleElement(dim.x / 2, dim.y / 2, 2.0)
            c.stroke().set(SvgColors.RED)

            val g = SvgGElement()
            g.children().add(hAxis)
            g.children().add(vAxis)
            g.children().add(c)
            return g
        }

        private fun createTextLabel(hAnchor: HorizontalAnchor, vAnchor: VerticalAnchor, angle: Double): TextLabel {
            val text = "Anchor: " + hAnchor.name + "-" + vAnchor.name + " angle: " + angle + "°"
            val label = TextLabel(text)
            label.addClassName(LABEL_CLASS_NAME)
            label.setHorizontalAnchor(hAnchor)
            label.setVerticalAnchor(vAnchor)
            label.rotate(angle)
            label.textColor().set(Color.DARK_BLUE)
            return label
        }
    }
}
