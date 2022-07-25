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
    private var isYOrientation: Boolean = false
    private var flipped: Boolean = false
    private var aesthetics: Aesthetics? = null
    private var aestheticMappers: Map<Aes<*>, ScaleMapper<*>>? = null
    private var aesBounds: DoubleRectangle? = null
    private var geomTargetCollector: GeomTargetCollector = NullGeomTargetCollector()

    constructor()

    private constructor(ctx: MyGeomContext) {
        isYOrientation = ctx.isYOrientation
        flipped = ctx.flipped
        aesthetics = ctx.aesthetics
        aestheticMappers = ctx.aestheticMappers
        aesBounds = ctx._aesBounds
        geomTargetCollector = ctx.targetCollector
    }

    override fun isYOrientation(isYOrientation: Boolean): ImmutableGeomContext.Builder {
        this.isYOrientation = isYOrientation
        return this
    }

    override fun flipped(flipped: Boolean): ImmutableGeomContext.Builder {
        this.flipped = flipped
        return this
    }

    override fun aesthetics(aesthetics: Aesthetics): ImmutableGeomContext.Builder {
        this.aesthetics = aesthetics
        return this
    }

    override fun aestheticMappers(aestheticMappers: Map<Aes<*>, ScaleMapper<*>>): ImmutableGeomContext.Builder {
        this.aestheticMappers = aestheticMappers
        return this
    }

    override fun aesBounds(aesBounds: DoubleRectangle): ImmutableGeomContext.Builder {
        this.aesBounds = aesBounds
        return this
    }

    override fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): ImmutableGeomContext.Builder {
        this.geomTargetCollector = geomTargetCollector
        return this
    }

    override fun build(): ImmutableGeomContext {
        return MyGeomContext(this)
    }


    private class MyGeomContext(b: GeomContextBuilder) : ImmutableGeomContext {
        val aesthetics = b.aesthetics
        val aestheticMappers = b.aestheticMappers
        val _aesBounds: DoubleRectangle? = b.aesBounds

        override val isYOrientation: Boolean = b.isYOrientation
        override val flipped: Boolean = b.flipped
        override val targetCollector = b.geomTargetCollector

        override fun getResolution(aes: Aes<Double>): Double {
            var resolution = 0.0
            if (aesthetics != null) {
                resolution = aesthetics.resolution(aes, 0.0)
            }
            if (resolution <= SeriesUtil.TINY) {
                resolution = getUnitResolution(aes)
            }

            return resolution
        }

        override fun getUnitResolution(aes: Aes<Double>): Double {
            val mapper = aestheticMappers?.get(aes) ?: Mappers.IDENTITY
            return mapper(1.0) as Double
        }

        override fun isMappedAes(aes: Aes<*>): Boolean {
            return aestheticMappers?.containsKey(aes) ?: false
        }

        override fun getAesBounds(): DoubleRectangle {
            check(_aesBounds != null) { "GeomContext: aesthetics bounds are not defined." }
            return _aesBounds
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
