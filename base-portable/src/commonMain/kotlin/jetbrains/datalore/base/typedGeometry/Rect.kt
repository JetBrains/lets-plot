/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

data class Rect<TypeT>(
    val origin: Vec<TypeT>,
    val dimension: Vec<TypeT>
) {

    companion object {
        fun <TypeT> LTRB(left: Double, top: Double, right: Double, bottom: Double): Rect<TypeT> {
            return Rect(Vec(left, top), Vec(right-left, bottom-top))
        }

        fun <TypeT> LTRB(leftTop: Vec<TypeT>, rightBottom: Vec<TypeT>): Rect<TypeT> {
            return Rect(leftTop, rightBottom - leftTop)
        }

        fun <TypeT> XYWH(x: Double, y: Double, width: Double, height: Double): Rect<TypeT> {
            return Rect(Vec(x, y), Vec(width, height))
        }

        fun <TypeT> XYWH(origin: Vec<TypeT>, dimension: Vec<TypeT>): Rect<TypeT> {
            return Rect(origin, dimension)
        }
    }
}
