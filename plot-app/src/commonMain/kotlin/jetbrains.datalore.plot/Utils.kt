package jetbrains.datalore.plot

import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.livemap.LiveMapUtil // LIVEMAP_SWITCH


fun injectLiveMap(
    plotSpec: MutableMap<String, Any>,
    assembler: PlotAssembler
) {
    LiveMapOptionsParser.parseFromPlotOptions(OptionsAccessor(plotSpec))
        ?.let { // LIVEMAP_SWITCH
            // LIVEMAP_SWITCH
            LiveMapUtil.injectLiveMapProvider( // LIVEMAP_SWITCH
                assembler.layersByTile, // LIVEMAP_SWITCH
                it // LIVEMAP_SWITCH
            ) // LIVEMAP_SWITCH
        } // LIVEMAP_SWITCH
}
