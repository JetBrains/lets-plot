/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.*

class MultilineLabelDemo: SimpleDemoBase(DEMO_BOX_SIZE) {

    override val cssStyle: String  // not working for JavaFx target (see)
        get() = "text {" +
                "font-size: 18px;" +
                "}"

    fun createModel(): GroupComponent {
        val specs = ArrayList<LabelSpec>()
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.BOTTOM))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.BOTTOM))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.BOTTOM))

        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.CENTER))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.CENTER))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.CENTER))

        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.TOP))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.TOP))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.TOP))

        val groupComponent = GroupComponent()

        val exampleDim = DoubleVector(200.0, 110.0)
        var y = 50
        specs.forEach { spec ->
            val labelExample = createLabelExample(
                exampleDim,
                spec.hAnchor,
                spec.vAnchor
            )
            SvgUtils.transformTranslate(labelExample, 300.0, y.toDouble())
            groupComponent.add(labelExample)
            y += exampleDim.y.toInt()
        }

        return groupComponent
    }

    private class LabelSpec(
        val hAnchor: Text.HorizontalAnchor,
        val vAnchor: Text.VerticalAnchor
    )

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(800.0, 1200.0)

        private fun createLabelExample(
            dim: DoubleVector,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor
        ): SvgGElement {
            val axis = TextLabelDemo.createAxis(dim)
            val textLabel = createTextLabel(
                hAnchor,
                vAnchor
            )
            textLabel.moveTo(dim.x / 2, dim.y / 2)

            val g = SvgGElement()
            g.children().add(axis)
            g.children().add(textLabel.rootGroup)
            return g
        }

        private fun createTextLabel(hAnchor: Text.HorizontalAnchor, vAnchor: Text.VerticalAnchor): MultilineLabel {
            val text = "Horizontal anchor:\n"+ hAnchor.name +
                    "\n\n" + // empty line
                    "Vertical anchor:\n"+vAnchor.name

            val label = MultilineLabel(text)
            label.setX(0.0)
            label.setHorizontalAnchor(hAnchor)
            label.setVerticalAnchor(vAnchor, 18.0)
            label.textColor().set(Color.DARK_BLUE)
            return label
        }
    }
}