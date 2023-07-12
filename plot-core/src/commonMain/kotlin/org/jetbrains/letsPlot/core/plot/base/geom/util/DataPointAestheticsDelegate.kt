/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

open class DataPointAestheticsDelegate(
    private val p: DataPointAesthetics
) : DataPointAesthetics() {

    final override fun index(): Int {
        return p.index()
    }

    final override fun group(): Int? {
        return p.group()
    }

    override fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): T? {
        return p.get(aes)
    }

    override val colorAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color> = p.colorAes

    override val fillAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color> = p.fillAes
}
