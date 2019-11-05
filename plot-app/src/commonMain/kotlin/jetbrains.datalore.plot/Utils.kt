/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.livemap.LiveMapUtil


fun injectLiveMap(
    plotSpec: MutableMap<String, Any>,
    assembler: PlotAssembler
) {
    LiveMapOptionsParser.parseFromPlotOptions(OptionsAccessor(plotSpec))
        ?.let {
            LiveMapUtil.injectLiveMapProvider(
                assembler.layersByTile,
                it
            )
        }
}
