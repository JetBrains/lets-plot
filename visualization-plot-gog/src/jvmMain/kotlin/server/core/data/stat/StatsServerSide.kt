package jetbrains.datalore.visualization.plot.gog.server.core.data.stat

object StatsServerSide {
    fun smooth(): SmoothStat {
        return SmoothStat()
    }

    fun density2d(): Density2dStat {
        return Density2dStat()
    }

    fun density2df(): Density2dfStat {
        return Density2dfStat()
    }
}
