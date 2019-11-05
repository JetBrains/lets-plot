/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Stat

class ContourStatBuilder {

    private var myBinCount = DEF_BIN_COUNT
    private var myBinWidth: Double? = null

    fun binCount(v: Int): ContourStatBuilder {
        myBinCount = v
        return this
    }

    fun binWidth(v: Double): ContourStatBuilder {
        myBinWidth = v
        return this
    }

    fun build(): Stat {
        return ContourStat(
            myBinCount,
            myBinWidth
        )
    }

    companion object {
        val DEF_BIN_COUNT = 10
    }
}
