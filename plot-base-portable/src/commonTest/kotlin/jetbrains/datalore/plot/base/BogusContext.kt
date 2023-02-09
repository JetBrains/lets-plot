/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.annotations.Annotations
import jetbrains.datalore.plot.base.interact.GeomTargetCollector

object BogusContext : GeomContext {
    override val flipped: Boolean
        get() = error("Not available in a bogus geom context")
    override val targetCollector: GeomTargetCollector
        get() = error("Not available in a bogus geom context")
    override val annotations: Annotations?
        get() = error("Not available in a bogus geom context")

    override fun getResolution(aes: Aes<Double>): Double {
        error("Not available in a bogus geom context")
    }

    override fun getAesBounds(): DoubleRectangle {
        error("Not available in a bogus geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        error("Not available in a bogus geom context")
    }

    override fun isMappedAes(aes: Aes<*>): Boolean {
        error("Not available in a bogus geom context")
    }

    override fun estimateTextSize(
        text: String,
        family: String,
        size: Double,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        error("Not available in a bogus geom context")
    }
}