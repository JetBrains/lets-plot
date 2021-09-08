/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.encode

import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.transform.PlotSpecTransform
import jetbrains.datalore.plot.config.transform.PlotSpecTransformUtil
import jetbrains.datalore.plot.config.transform.SpecChange

object DataSpecEncodeTransforms {
    private fun addDataChanges(builder: PlotSpecTransform.Builder, change: SpecChange, isGGBunch: Boolean): PlotSpecTransform.Builder {

        val specSelectors = PlotSpecTransformUtil.getPlotAndLayersSpecSelectors(isGGBunch, DATA)
        for (specSelector in specSelectors) {
            builder.change(specSelector, change)
        }
        return builder
    }

    fun clientSideDecode(isGGBunch: Boolean): PlotSpecTransform {
        val builder = PlotSpecTransform.builderForRawSpec()
        addDataChanges(builder, ClientSideDecodeChange(), isGGBunch)
        addDataChanges(builder, ClientSideDecodeOldStyleChange(), isGGBunch)
        return builder.build()
    }
}
