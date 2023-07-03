/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformable.Companion.TRANSFORM

class SvgClipPathElement : SvgGraphicsElement(),
    SvgTransformable {

    companion object {
        private val CLIP_PATH_UNITS: SvgAttributeSpec<ClipPathUnits> =
            SvgAttributeSpec.createSpec("clipPathUnits")
    }

    override val elementName: String
        get() = "clipPath"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    fun clipPathUnits(): Property<ClipPathUnits?> {
        return getAttribute(CLIP_PATH_UNITS)
    }

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }

    enum class ClipPathUnits(private val myAttributeString: String) {
        USER_SPACE_ON_USE("userSpaceOnUse"),
        OBJECT_BOUNDING_BOX("objectBoundingBox");


        override fun toString(): String {
            return myAttributeString
        }
    }
}
