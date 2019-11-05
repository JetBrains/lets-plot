/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.interact.GeomTargetCollector

interface GeomContext {
    val targetCollector: GeomTargetCollector

    fun getResolution(aes: Aes<Double>): Double

    fun getUnitResolution(aes: Aes<Double>): Double

    fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext
}
