package jetbrains.datalore.plot

import jetbrains.datalore.plot.config.LiveMapOptionsParser
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import kotlin.test.Test

// {data={
//      lat=[34.835461, 38.843142],
//      lon=[-100.420313, -91.016016]
//  },
//  kind=plot,
//  scales=[],
//  layers=[
//      {
//          data={},
//          geom=livemap,
//          mapping={}
//      }, {
//          data={},
//          geom=point,
//          mapping={x=lon, y=lat}
//      }
//  ]
//}

class PlotBuilderTest {


    private fun createPlot(
        initPlotSpec: MutableMap<String, Any>
    ): jetbrains.datalore.plot.builder.Plot {
        val plotSpec = PlotConfigClientSide.processTransform(initPlotSpec)

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(plotSpec)

        LiveMapOptionsParser.parseFromPlotOptions(OptionsAccessor(plotSpec))
            ?.let { jetbrains.livemap.geom.LiveMapUtil.injectLiveMapProvider(assembler.layersByTile, it) } // LIVEMAP_SWITCH

        return assembler.createPlot()
    }

    private fun dict(vararg pairs: Pair<String, Any>): HashMap<String, Any> {
        return HashMap<String, Any>().apply { putAll(pairs.asList()) }
    }

    fun list(vararg objs: Any): MutableList<Any> {
        return objs.toMutableList()
    }

    @Test
    fun missingMapper() {
        createPlot(dict(
            "data" to
                    dict(
                        "lat" to list(34.835461, 38.843142),
                        "lon" to list(-100.420313, -91.016016)
                    ),
            "kind" to "plot",
            "scales" to list(),
            "layers" to list(
                dict(
                    "data" to dict(),
                    "geom" to "livemap",
                    "mapping" to dict()
                ),
                dict(
                    "data" to dict(),
                    "geom" to "point",
                    "mapping" to dict(
                        "x" to "lon",
                        "y" to "lat"
                    )
                )
            )
        )).rootGroup
    }

    @Test
    fun missingChart() {
        createPlot(dict(
            "data" to
                    dict(
                        "order" to list(1,2,3,1,2,3),
                        "val" to list(300, 110, 200, 234, 500, -100),
                        "map_id" to list("-100,34", "-100,34", "-100,34", "-91,38", "-91,38", "-91,38")
                    ),
            "kind" to "plot",
            "scales" to list(),
            "layers" to list(
                dict(
                    "data" to dict(),
                    "geom" to "livemap",
                    "display_mode" to "pie",
                    "mapping" to dict(
                        "x" to "order",
                        "y" to "val",
                        "map_id" to "map_id",
                        "fill" to "order"
                    )
                )
            )
        )).rootGroup
    }
}