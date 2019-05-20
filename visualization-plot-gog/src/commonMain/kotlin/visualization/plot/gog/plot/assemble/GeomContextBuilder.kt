package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.event3.NullGeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext

class GeomContextBuilder : ImmutableGeomContext.Builder {
    private var myAesthetics: Aesthetics? = null
    private var myAestheticMappers: Map<Aes<*>, (Double?) -> Any?>? = null
    private var myGeomTargetCollector: GeomTargetCollector = NullGeomTargetCollector()

    constructor()

    private constructor(ctx: MyGeomContext) {
        myAesthetics = ctx.myAesthetics
        myAestheticMappers = ctx.myAestheticMappers
    }

    override fun aesthetics(aesthetics: Aesthetics?): ImmutableGeomContext.Builder {
        myAesthetics = aesthetics
        return this
    }

    override fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): ImmutableGeomContext.Builder {
        myAestheticMappers = aestheticMappers
        return this
    }

    override fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): ImmutableGeomContext.Builder {
        myGeomTargetCollector = geomTargetCollector
        return this
    }

    override fun build(): ImmutableGeomContext {
        return MyGeomContext(this)
    }


    private class MyGeomContext(b: GeomContextBuilder) : ImmutableGeomContext {
        val myAesthetics = b.myAesthetics
        val myAestheticMappers = b.myAestheticMappers
        override val targetCollector = b.myGeomTargetCollector

        override fun getResolution(aes: Aes<Double>): Double {
            var resolution = 0.0
            if (myAesthetics != null) {
                resolution = myAesthetics.resolution(aes, 0.0)
            }
            if (resolution <= SeriesUtil.TINY) {
                resolution = getUnitResolution(aes)
            }

            return resolution
        }

        override fun getUnitResolution(aes: Aes<Double>): Double {
            var resolution = 1.0
            if (myAestheticMappers != null && myAestheticMappers.containsKey(aes)) {
                val mapper = myAestheticMappers[aes]
                resolution = mapper!!(resolution) as Double
            }

            return resolution
        }

        override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
            return GeomContextBuilder()
                    .aesthetics(myAesthetics)
                    .aestheticMappers(myAestheticMappers)
                    .geomTargetCollector(targetCollector)
                    .build()
        }

        override fun with(): ImmutableGeomContext.Builder {
            return GeomContextBuilder(this)
        }
    }
}
