/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.typedGeometry.Transforms.transform
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.core.*
import jetbrains.livemap.core.Transforms
import jetbrains.livemap.mapengine.MapProjection
import kotlin.math.min

internal class MapProjectionBuilder(
    private val geoProjection: GeoProjection,
    private val mapRect: WorldRectangle
) {
    var reverseX = false
    var reverseY = false

    private fun offset(offset: Double): Transform<Double, Double> {
        return object : Transform<Double, Double> {
            override fun apply(v: Double): Double = v - offset
            override fun invert(v: Double): Double = v + offset
        }
    }

    private fun <InT, InterT, OutT> composite(
        t1: UnsafeTransform<InT, InterT>,
        t2: UnsafeTransform<InterT, OutT>
    ): UnsafeTransform<InT, OutT> {
        return object : UnsafeTransform<InT, OutT> {
            override fun apply(v: InT): OutT? = v.run(t1::apply)?.run(t2::apply)
            override fun invert(v: OutT): InT? = v.run(t2::invert)?.run(t1::invert)
        }
    }

    private fun <InT, InterT, OutT> composite(
        t1: UnsafeTransform<InT, InterT>,
        t2: Transform<InterT, OutT>
    ): UnsafeTransform<InT, OutT> {
        return object : UnsafeTransform<InT, OutT> {
            override fun apply(v: InT): OutT? = v.run(t1::apply)?.run(t2::apply)
            override fun invert(v: OutT): InT? = v.run(t2::invert)?.run(t1::invert)
        }
    }

    private fun <InT, InterT, OutT> composite(
        t1: Transform<InT, InterT>,
        t2: Transform<InterT, OutT>
    ): Transform<InT, OutT> {
        return object : Transform<InT, OutT> {
            override fun apply(v: InT): OutT = v.run(t1::apply).run(t2::apply)
            override fun invert(v: OutT): InT = v.run(t2::invert).run(t1::invert)
        }
    }

    private fun linear(offset: Double, scale: Double): Transform<Double, Double> {
        return composite(offset(offset), Transforms.scale { scale })
    }

    fun create(): MapProjection {
        val rect = transform(geoProjection.validRect(), geoProjection::apply)
            ?: error("Unable to transform projection valid rect")
        val scale = min(mapRect.width / rect.width, mapRect.height / rect.height)

        @Suppress("UNCHECKED_CAST")
        val projSize = (mapRect.dimension * (1.0 / scale)) as Vec<Geographic>
        val projRect = Rect(rect.center - projSize * 0.5, projSize)

        val offsetX = if (reverseX) projRect.right else projRect.left
        val scaleX = if (reverseX) -scale else scale
        val offsetY = if (reverseY) projRect.bottom else projRect.top
        val scaleY = if (reverseY) -scale else scale

        val linearProjection =
            Transforms.tuple<Geographic, World>(
                linear(offsetX, scaleX),
                linear(offsetY, scaleY)
            )

        val proj = composite(geoProjection, linearProjection)

        return object : MapProjection {
            override fun apply(v: LonLatPoint): WorldPoint? = proj.apply(v)
            override fun invert(v: WorldPoint): LonLatPoint? = proj.invert(v)

            override val mapRect: WorldRectangle
                get() = this@MapProjectionBuilder.mapRect
        }
    }
}
