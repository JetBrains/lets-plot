package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.Density2dStat
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.Density2dfStat
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.SmoothStat

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
