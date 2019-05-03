package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.visualization.plot.gog.config.StatProto
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Density2dStatShell
import jetbrains.datalore.visualization.plot.gog.core.data.stat.SmoothStatShell
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.StatsServerSide

internal class StatProtoServerSide : StatProto() {
    override fun createSmoothStat(): SmoothStatShell {
        return StatsServerSide.smooth()
    }

    override fun createDensity2dStat(): Density2dStatShell {
        return StatsServerSide.density2d()
    }

    override fun createDensity2dfStat(): Density2dStatShell {
        return StatsServerSide.density2df()
    }
}
