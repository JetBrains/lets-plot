/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromFill
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params

class DensityGeom : AreaGeom() {

    override fun setupTooltipParams(aes: DataPointAesthetics, ctx: GeomContext): TooltipParams {
       val colorsByDataPoint = when {
            ctx.isMappedAes(Aes.FILL) && aes.alpha()!! > 0 -> {
                listOf(
                    fromFill(aes),
                    aes.color().takeIf { ctx.isMappedAes(Aes.COLOR) && AesScaling.strokeWidth(aes) > 0 }
                )
            }
            else -> {
                listOf(aes.color().takeIf { AesScaling.strokeWidth(aes) > 0 })
            }
        }.filterNotNull()

        return params()
            .setMainColor(fromColor(aes))
            .setColors(colorsByDataPoint)
    }

    companion object {
//        val RENDERS: List<Aes<*>> = AreaGeom.RENDERS

        const val HANDLES_GROUPS =
            AreaGeom.HANDLES_GROUPS
    }


}
