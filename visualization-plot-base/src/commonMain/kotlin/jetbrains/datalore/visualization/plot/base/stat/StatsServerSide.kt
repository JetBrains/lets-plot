package jetbrains.datalore.visualization.plot.base.stat

expect object StatsServerSide {
    fun smooth(): SmoothStat

    fun density2d(): AbstractDensity2dStat

    fun density2df(): AbstractDensity2dStat
}
