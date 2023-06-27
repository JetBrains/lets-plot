/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty

class SvgTextNode(text: String) : SvgNode() {

    private val myContent: Property<String>

    init {
        myContent = ValueProperty(text)
    }

    fun textContent(): Property<String> {
        return myContent
    }

    override fun children(): ObservableList<SvgNode> {
        return NO_CHILDREN_LIST
    }

    override fun toString(): String {
        return textContent().get()
    }

    companion object {
        private val NO_CHILDREN_LIST: ObservableArrayList<SvgNode> = object : ObservableArrayList<SvgNode>() {
            override fun checkAdd(index: Int, item: SvgNode) {
                throw UnsupportedOperationException("Cannot add children to SvgTextNode")
            }

            override fun checkRemove(index: Int, item: SvgNode) {
                throw UnsupportedOperationException("Cannot remove children from SvgTextNode")
            }
        }
    }
}