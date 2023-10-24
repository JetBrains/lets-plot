/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.component

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class FormulaDemo : SimpleDemoBase(DEMO_BOX_SIZE) {
    fun createModel(): GroupComponent {
        val groupComponent = GroupComponent()

        val label = MultilineLabel("""-1.5·\(10^{-25}\)""")
        label.moveTo(LABEL_POSITION.x, LABEL_POSITION.y)

        val labelNode = SvgGElement()
        labelNode.children().add(label.rootGroup)

        groupComponent.add(labelNode)
        return groupComponent
    }

    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(100.0, 50.0)
        private val LABEL_POSITION = DoubleVector(25.0, 25.0)
    }
}