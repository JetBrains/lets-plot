package jetbrains.datalore.visualization.plot.server.config

import jetbrains.datalore.visualization.plot.base.stat.AbstractDensity2dStat
import jetbrains.datalore.visualization.plot.base.stat.SmoothStat
import jetbrains.datalore.visualization.plot.base.stat.StatsServerSide
import jetbrains.datalore.visualization.plot.config.StatProto

internal class StatProtoServerSide : StatProto() {
    override fun createSmoothStat(): SmoothStat {
        return StatsServerSide.smooth()
    }

    override fun createDensity2dStat(): AbstractDensity2dStat {
        return StatsServerSide.density2d()
    }

    override fun createDensity2dfStat(): AbstractDensity2dStat {
        return StatsServerSide.density2df()
    }
}
