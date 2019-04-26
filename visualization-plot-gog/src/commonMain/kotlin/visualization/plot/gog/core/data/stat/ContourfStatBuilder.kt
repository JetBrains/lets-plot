package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.visualization.plot.gog.core.data.Stat

class ContourfStatBuilder {

    private var myBinCount = DEF_BIN_COUNT
    private var myBinWidth: Double? = null

    fun binCount(v: Int): ContourfStatBuilder {
        myBinCount = v
        return this
    }

    fun binWidth(v: Double): ContourfStatBuilder {
        myBinWidth = v
        return this
    }

    fun build(): Stat {
        return ContourfStat(
                myBinCount,
                myBinWidth
        )
    }

    companion object {
        val DEF_BIN_COUNT = 10
    }
}
