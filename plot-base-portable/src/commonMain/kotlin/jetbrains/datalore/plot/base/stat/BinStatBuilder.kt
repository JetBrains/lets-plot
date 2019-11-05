/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Stat

class BinStatBuilder {

    private var myBinCount = DEF_BIN_COUNT
    private var myBinWidth: Double? = null
    private var myCenter: Double? = null
    private var myBoundary: Double? = null

    fun binCount(v: Int): BinStatBuilder {
        myBinCount = v
        return this
    }

    fun binWidth(v: Double): BinStatBuilder {
        myBinWidth = v
        return this
    }

    fun center(v: Double): BinStatBuilder {
        myCenter = v
        return this
    }

    fun boundary(v: Double): BinStatBuilder {
        myBoundary = v
        return this
    }

    fun build(): Stat {
        var xPosKind = BinStat.XPosKind.NONE
        var xPosValue = 0.0
        if (myBoundary != null) {
            xPosKind = BinStat.XPosKind.BOUNDARY
            xPosValue = myBoundary!!
        } else if (myCenter != null) {
            xPosKind = BinStat.XPosKind.CENTER
            xPosValue = myCenter!!
        }

        return BinStat(
            myBinCount,
            myBinWidth,
            xPosKind,
            xPosValue
        )
    }

    companion object {
        val DEF_BIN_COUNT = 30
    }
}
