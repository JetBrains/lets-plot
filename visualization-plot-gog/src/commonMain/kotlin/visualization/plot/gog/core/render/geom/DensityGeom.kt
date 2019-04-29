package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.HintColorUtil.fromColor

internal class DensityGeom : AreaGeom() {

    override fun setupTooltipParams(aes: DataPointAesthetics): TooltipParams {
        return params().setColor(fromColor(aes))
    }

    companion object {
        val RENDERS: List<Aes<*>> = AreaGeom.RENDERS

        val HANDLES_GROUPS = AreaGeom.HANDLES_GROUPS
    }


}
