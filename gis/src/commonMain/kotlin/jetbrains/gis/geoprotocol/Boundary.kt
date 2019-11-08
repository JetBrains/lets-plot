/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.MultiPolygon

interface Boundary<TypeT> {

    fun asMultipolygon(): MultiPolygon<TypeT>

    companion object {
        fun <TypeT> create(points: MultiPolygon<TypeT>): Boundary<TypeT> {
            return object : Boundary<TypeT> {
                override fun asMultipolygon(): MultiPolygon<TypeT> {
                    return points
                }
            }
        }
    }
}
