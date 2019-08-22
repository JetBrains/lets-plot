package jetbrains.datalore.visualization.plot.base.stat

expect object StatsServerSide {
    fun smooth(): SmoothStat

    fun density2d(): Density2dStatShell

    fun density2df(): Density2dStatShell
}
