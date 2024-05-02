/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

class SvgAttributeSpec<ValueT> private constructor(val name: String, val namespaceUri: String?) {

    companion object {
        fun <ValueT> createSpec(name: String): SvgAttributeSpec<ValueT> {
            return SvgAttributeSpec(name, null)
        }

        fun <ValueT> createSpecNS(name: String, prefix: String, namespaceUri: String): SvgAttributeSpec<ValueT> {
            return SvgAttributeSpec("$prefix:$name", namespaceUri)
        }
    }

    fun hasNamespace(): Boolean {
        return namespaceUri != null
    }

    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SvgAttributeSpec<*>
        if (name != other.name) return false
        return true
    }
}