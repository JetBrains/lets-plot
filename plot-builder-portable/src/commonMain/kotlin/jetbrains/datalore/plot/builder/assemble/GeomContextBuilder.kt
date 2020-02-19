/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.common.data.SeriesUtil

class GeomContextBuilder : ImmutableGeomContext.Builder {
    private var myAesthetics: Aesthetics? = null
    private var myAestheticMappers: Map<Aes<*>, (Double?) -> Any?>? = null
    private var myGeomTargetCollector: GeomTargetCollector =
        NullGeomTargetCollector()

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


    private class MyGeomContext(b: GeomContextBuilder) :
        ImmutableGeomContext {
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
