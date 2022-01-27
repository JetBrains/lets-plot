/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.common.data.SeriesUtil

class GeomContextBuilder : ImmutableGeomContext.Builder {
    private var myFlipped: Boolean = false
    private var myAesthetics: Aesthetics? = null
    private var myAestheticMappers: Map<Aes<*>, ScaleMapper<*>>? = null
    private var myAesBounds: DoubleRectangle? = null
    private var myGeomTargetCollector: GeomTargetCollector = NullGeomTargetCollector()

    constructor()

    private constructor(ctx: MyGeomContext) {
        myFlipped = ctx.flipped
        myAesthetics = ctx.myAesthetics
        myAestheticMappers = ctx.myAestheticMappers
        myAesBounds = ctx.myAesBounds
        myGeomTargetCollector = ctx.targetCollector
    }

    override fun flipped(flipped: Boolean): ImmutableGeomContext.Builder {
        myFlipped = flipped
        return this
    }

    override fun aesthetics(aesthetics: Aesthetics): ImmutableGeomContext.Builder {
        myAesthetics = aesthetics
        return this
    }

    override fun aestheticMappers(aestheticMappers: Map<Aes<*>, ScaleMapper<*>>): ImmutableGeomContext.Builder {
        myAestheticMappers = aestheticMappers
        return this
    }

    override fun aesBounds(aesBounds: DoubleRectangle): ImmutableGeomContext.Builder {
        myAesBounds = aesBounds
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
        val myAesBounds: DoubleRectangle? = b.myAesBounds

        override val flipped: Boolean = b.myFlipped
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
            val mapper = myAestheticMappers?.get(aes) ?: Mappers.IDENTITY
            return mapper(1.0) as Double
        }

        override fun isMappedAes(aes: Aes<*>): Boolean {
            return myAestheticMappers?.containsKey(aes) ?: false
        }

        override fun getAesBounds(): DoubleRectangle {
            check(myAesBounds != null) { "GeomContext: aesthetics bounds are not defined." }
            return myAesBounds
        }

        override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
            return with()
                .geomTargetCollector(targetCollector)
                .build()
        }

        override fun with(): ImmutableGeomContext.Builder {
            return GeomContextBuilder(this)
        }
    }
}
