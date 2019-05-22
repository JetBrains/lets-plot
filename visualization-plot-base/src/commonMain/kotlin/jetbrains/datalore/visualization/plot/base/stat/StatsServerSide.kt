package jetbrains.datalore.visualization.plot.base.stat

expect object StatsServerSide {
    fun smooth(): SmoothStatShell

    fun density2d(): Density2dStatShell

    fun density2df(): Density2dStatShell
}
