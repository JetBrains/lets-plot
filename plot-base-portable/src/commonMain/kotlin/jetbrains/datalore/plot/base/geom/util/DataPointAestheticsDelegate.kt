/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics

open class DataPointAestheticsDelegate(
    private val p: DataPointAesthetics
) : DataPointAesthetics() {

    final override fun index(): Int {
        return p.index()
    }

    final override fun group(): Int? {
        return p.group()
    }

    override fun <T> get(aes: Aes<T>): T? {
        return p.get(aes)
    }

    override val aesColor: Aes<Color> = p.aesColor

    override val aesFill: Aes<Color> = p.aesFill
}
