/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.frame

import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.FrameOfReference
import jetbrains.datalore.plot.builder.GeomLayer

internal class BogusFrameOfReference : FrameOfReference {

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        throw IllegalStateException("Bogus frame of reference is not supposed to be used.")
    }
}