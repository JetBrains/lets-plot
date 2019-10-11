package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.common.data.SeriesUtil

class GeomContextBuilder : jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder {
    private var myAesthetics: Aesthetics? = null
    private var myAestheticMappers: Map<Aes<*>, (Double?) -> Any?>? = null
    private var myGeomTargetCollector: GeomTargetCollector =
        NullGeomTargetCollector()

    constructor()

    private constructor(ctx: jetbrains.datalore.plot.builder.assemble.GeomContextBuilder.MyGeomContext) {
        myAesthetics = ctx.myAesthetics
        myAestheticMappers = ctx.myAestheticMappers
    }

    override fun aesthetics(aesthetics: Aesthetics?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder {
        myAesthetics = aesthetics
        return this
    }

    override fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder {
        myAestheticMappers = aestheticMappers
        return this
    }

    override fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder {
        myGeomTargetCollector = geomTargetCollector
        return this
    }

    override fun build(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext {
        return jetbrains.datalore.plot.builder.assemble.GeomContextBuilder.MyGeomContext(this)
    }


    private class MyGeomContext(b: jetbrains.datalore.plot.builder.assemble.GeomContextBuilder) :
        jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext {
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
            return jetbrains.datalore.plot.builder.assemble.GeomContextBuilder()
                    .aesthetics(myAesthetics)
                    .aestheticMappers(myAestheticMappers)
                    .geomTargetCollector(targetCollector)
                    .build()
        }

        override fun with(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder {
            return jetbrains.datalore.plot.builder.assemble.GeomContextBuilder(this)
        }
    }
}
