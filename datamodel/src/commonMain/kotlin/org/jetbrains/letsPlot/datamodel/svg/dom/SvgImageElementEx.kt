/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.registration.Registration

/**
 * This 'element' is not a part of SVG specification.
 * During mapping process it will be mapped to svg image element where 'bitmap' is encoded as image data url
 * and set as a value of 'href' attribute.
 */
class SvgImageElementEx(x: Double, y: Double, width: Double, height: Double, private val myBitmap: Bitmap) :

        SvgImageElement(x, y, width, height) {

    override fun href(): Property<String?> {
        // Make href read-only
        // The 'href' shouldn't be present in the set returned by SvgElement#getAttributeKeys()
        val hrefProp = super.href()
        return object : Property<String?> {
            override val propExpr: String
                get() = hrefProp.propExpr

            override fun get(): String? {
                return hrefProp.get()
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out String?>>): Registration {
                return hrefProp.addHandler(handler)
            }

            override fun set(value: String?) {
                throw IllegalStateException("href property is read-only in " + this@SvgImageElementEx::class.simpleName)
            }
        }
    }

    fun asImageElement(encoder: RGBEncoder): SvgImageElement {
        val imageElement = SvgImageElement()
        SvgUtils.copyAttributes(this, imageElement)

        val hrefValue = encoder.toDataUrl(
                myBitmap.width,
                myBitmap.height,
                myBitmap.argbValues
        )
        imageElement.href().set(hrefValue)
        return imageElement
    }

    interface RGBEncoder {
        fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String
    }

    class Bitmap
    /**
     * @param argbValues image binary data.
     * Each element of the array represents a pixel,
     * where alpha, red, green, blue values are in the range [0..255] and are packed into four bytes.
     * The array is filled by-row.
     */
    (val width: Int, val height: Int, argbValues: IntArray) {
        val argbValues: IntArray = intArrayOf(*argbValues)
    }
}
