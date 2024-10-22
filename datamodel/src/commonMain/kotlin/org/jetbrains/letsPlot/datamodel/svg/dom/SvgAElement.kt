/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property

class SvgAElement : SvgElement() {

    companion object {
        val HREF = SvgAttributeSpec.createSpec<String>("href")
        val XLINK_HREF = SvgAttributeSpec.createSpecNS<String>("href", "xlink", XmlNamespace.XLINK_NAMESPACE_URI)
    }

    override val elementName: String
        get() = "a"

    fun href(): Property<String?> {
        return getAttribute(HREF)
    }

    fun xlinkHref(): Property<String?> {
        return getAttribute(XLINK_HREF)
    }
}
