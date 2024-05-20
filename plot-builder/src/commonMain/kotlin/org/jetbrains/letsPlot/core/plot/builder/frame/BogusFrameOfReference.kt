/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer

internal class BogusFrameOfReference : FrameOfReference {
    override fun zoom(scale: Double) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun pan(from: DoubleVector, to: DoubleVector): DoubleVector? {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun setClip(element: SvgComponent) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }
}