/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params

class DensityGeom : AreaGeom() {

    override fun setupTooltipParams(aes: DataPointAesthetics): TooltipParams {
        return params().setMainColor(fromColor(aes))
    }

    companion object {
//        val RENDERS: List<Aes<*>> = AreaGeom.RENDERS

        const val HANDLES_GROUPS =
            AreaGeom.HANDLES_GROUPS
    }


}
