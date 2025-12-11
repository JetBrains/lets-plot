/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap

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

    fun asImageElement(): SvgImageElement {
        val imageElement = SvgImageElement()
        SvgUtils.copyAttributes(this, imageElement)

        val hrefValue = Png.encodeDataImage(myBitmap)
        imageElement.href().set(hrefValue)
        return imageElement
    }
}
