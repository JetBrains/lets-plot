/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformable.Companion.TRANSFORM
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.XLINK_NAMESPACE_URI
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.XLINK_PREFIX

open class SvgImageElement() : SvgGraphicsElement(),
    SvgTransformable {

    companion object {
        val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
        val WIDTH: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec(SvgConstants.WIDTH)
        val HEIGHT: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec(SvgConstants.HEIGHT)
        val HREF: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpecNS(
                "href",
                XLINK_PREFIX,
                XLINK_NAMESPACE_URI
            )
        val PRESERVE_ASPECT_RATIO: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec("preserveAspectRatio")
    }

    override val elementName = "image"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    init {
        setAttribute(PRESERVE_ASPECT_RATIO, "none")
    }

    constructor(x: Double, y: Double, width: Double, height: Double) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setAttribute(WIDTH, width)
        setAttribute(HEIGHT, height)
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
        return getAttribute(Y)
    }

    fun width(): Property<Double?> {
        return getAttribute(WIDTH)
    }

    fun height(): Property<Double?> {
        return getAttribute(HEIGHT)
    }

    open fun href(): Property<String?> {
        return getAttribute(HREF)
    }

    fun preserveAspectRatio(): Property<String?> {
        return getAttribute(PRESERVE_ASPECT_RATIO)
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
}
