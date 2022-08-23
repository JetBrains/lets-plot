/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.interact.GeomTargetCollector

interface GeomContext {
    val flipped: Boolean
    val targetCollector: GeomTargetCollector

    fun getResolution(aes: Aes<Double>): Double

    // ToDo: remove
    fun getUnitResolution(aes: Aes<Double>): Double

    /**
     * @return  A rectangle which origin and size are computed basing on
     *          the overall ranges of X,Y (and other positional) aesthetics.
     */
    fun getAesBounds(): DoubleRectangle

    fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext

    fun isMappedAes(aes: Aes<*>): Boolean
}
