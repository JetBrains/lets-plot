/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

/**
 * Typed key object for putting user data into [MappingContext]
 */
class MappingContextProperty<ValueT>(private val myName: String) {

    override fun toString(): String {
        return "MappingContextProperty[$myName]"
    }
}