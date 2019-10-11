package jetbrains.datalore.plot

import jetbrains.datalore.plot.config.*
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
            ?.let { jetbrains.livemap.geom.LiveMapUtil.injectLiveMapProvider(assembler.layersByTile, it) }

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
}