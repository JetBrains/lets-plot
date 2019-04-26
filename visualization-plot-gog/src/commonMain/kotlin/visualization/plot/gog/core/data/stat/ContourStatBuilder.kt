package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.visualization.plot.gog.core.data.Stat

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
