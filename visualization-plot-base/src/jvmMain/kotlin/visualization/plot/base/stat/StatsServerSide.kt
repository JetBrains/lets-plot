package jetbrains.datalore.visualization.plot.base.stat

actual object StatsServerSide {
    actual fun smooth() = SmoothStat()

    actual fun density2d(): AbstractDensity2dStat = Density2dStat()

    actual fun density2df(): AbstractDensity2dStat = Density2dfStat()
}
