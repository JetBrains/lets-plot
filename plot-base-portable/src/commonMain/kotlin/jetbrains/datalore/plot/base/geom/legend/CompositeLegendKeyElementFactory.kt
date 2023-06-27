/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.legend

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class CompositeLegendKeyElementFactory(vararg factories: LegendKeyElementFactory) :
    LegendKeyElementFactory {
    private val factories = factories

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val g = SvgGElement()
        for (factory in factories) {
            g.children().add(factory.createKeyElement(p, size))
        }
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
//        var minWidth = 0.0
//        var minHeight = 0.0
//        for (factory in factories) {
//            val keySize = factory.minimumKeySize(p)
//            minWidth = max(minWidth, keySize.x)
//            minHeight = max(minHeight, keySize.y)
//        }
//        return DoubleVector(minWidth, minHeight)
        return super.minimumKeySize(p)
    }
}