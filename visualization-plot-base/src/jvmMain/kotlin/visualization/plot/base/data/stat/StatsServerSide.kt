package jetbrains.datalore.visualization.plot.gog.server.core.data.stat

import jetbrains.datalore.visualization.plot.base.data.stat.Density2dStatShell
import jetbrains.datalore.visualization.plot.base.data.stat.SmoothStatShell

actual object StatsServerSide {
    actual fun smooth(): SmoothStatShell {
        return SmoothStat()
    }

    actual fun density2d(): Density2dStatShell {
        return Density2dStat()
    }

    actual fun density2df(): Density2dStatShell {
        return Density2dfStat()
    }
}
