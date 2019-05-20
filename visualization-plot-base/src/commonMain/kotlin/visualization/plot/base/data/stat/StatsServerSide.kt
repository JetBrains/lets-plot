package jetbrains.datalore.visualization.plot.gog.server.core.data.stat

import jetbrains.datalore.visualization.plot.base.data.stat.Density2dStatShell
import jetbrains.datalore.visualization.plot.base.data.stat.SmoothStatShell

expect object StatsServerSide {
    fun smooth(): SmoothStatShell

    fun density2d(): Density2dStatShell

    fun density2df(): Density2dStatShell
}
