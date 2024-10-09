/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.max
import kotlin.math.min

object SvgUtils {
    private val OPACITY_TABLE: DoubleArray = DoubleArray(256)

    init {
        for (alpha in 0..255) {
            OPACITY_TABLE[alpha] = alpha / 255.0
        }
    }

    fun opacity(c: Color): Double {
        return OPACITY_TABLE[c.alpha]
    }

    fun alpha2opacity(colorAlpha: Int): Double {
        return OPACITY_TABLE[colorAlpha]
    }

    fun toARGB(c: Color): Int {
        return toARGB(c.red, c.green, c.blue, c.alpha)
    }

    fun toARGB(c: Color, alpha: Double): Int {
        return toARGB(
            c.red,
            c.green,
            c.blue,
            max(0.0, min(255.0, alpha * 255)).toInt()
        )
    }

    fun toARGB(r: Int, g: Int, b: Int, alpha: Int): Int {
        val rgb = (r shl 16) + (g shl 8) + b
        return (alpha shl 24) + rgb
    }

    internal fun colorAttributeTransform(color: Property<SvgColor?>, opacity: Property<Double?>): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                color.set(SvgColors.create(value))
                if (value != null) {
                    opacity.set(opacity(value))
                } else {
                    opacity.set(1.0)
                }
            }
        }
    }

    fun transformMatrix(element: SvgTransformable, a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
        element.transform().set(SvgTransformBuilder().matrix(a, b, c, d, e, f).build())
    }

    fun transformTranslate(element: SvgTransformable, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().translate(x, y).build())
    }

    fun transformTranslate(element: SvgTransformable, vector: DoubleVector) {
        transformTranslate(element, vector.x, vector.y)
    }

    fun transformTranslate(element: SvgTransformable, x: Double) {
        element.transform().set(SvgTransformBuilder().translate(x).build())
    }

    fun transformScale(element: SvgTransformable, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().scale(x, y).build())
    }

    fun transformScale(element: SvgTransformable, x: Double) {
        element.transform().set(SvgTransformBuilder().scale(x).build())
    }

    fun transformRotate(element: SvgTransformable, a: Double, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().rotate(a, x, y).build())
    }

    fun transformRotate(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().rotate(a).build())
    }

    fun transformSkewX(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().skewX(a).build())
    }

    fun transformSkewY(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().skewY(a).build())
    }

    fun copyAttributes(source: SvgElement, target: SvgElement) {
        for (attributeSpec in source.attributeKeys) {
            @Suppress("UNCHECKED_CAST")
            val spec = attributeSpec as SvgAttributeSpec<Any?>
            target.setAttribute(spec, source.getAttribute(attributeSpec).get())
        }
    }

    fun pngDataURI(base64EncodedPngImage: String): String {
        return StringBuilder("data:image/png;base64,")
                .append(base64EncodedPngImage)
                .toString()
    }

    // Useful for debugging
    fun getRoot(node: SvgNode): SvgNode {
        tailrec fun findRoot(currNode: SvgNode): SvgNode {
            return if (currNode.parent().get() == null) {
                currNode
            } else {
                findRoot(currNode.parent().get()!!)
            }
        }
        return findRoot(node)
    }

    fun breadthFirstTraversal(node: SvgNode): Sequence<SvgNode> {
        fun collectChildren(node: SvgNode): Sequence<SvgNode> {
            return node.children().asSequence() + node.children().asSequence().flatMap(::collectChildren)
        }
        return sequenceOf(node) + collectChildren(node)
    }

    fun depthFirstTraversal(node: SvgNode): Sequence<SvgNode> {
        return sequenceOf(node) + node.children().asSequence().flatMap(::depthFirstTraversal)
    }

    fun findNodeById(node: SvgNode, id: String): SvgNode? {
        return breadthFirstTraversal(node).find { (it as? SvgElement)?.id()?.get() == id }
    }
}
