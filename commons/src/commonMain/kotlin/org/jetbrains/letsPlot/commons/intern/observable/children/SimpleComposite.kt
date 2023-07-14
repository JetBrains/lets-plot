/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.children

import org.jetbrains.letsPlot.commons.intern.observable.property.DelayedValueProperty
import kotlin.js.JsName

open class SimpleComposite<ParentT, SiblingT> {
    private val myParent = DelayedValueProperty<ParentT>()
    private var myPositionData: PositionData<out SiblingT>? = null

    val position: Position<out SiblingT>
        get() {
            if (myPositionData == null) {
                throw IllegalStateException()
            }
            return myPositionData!!.get()
        }

    fun removeFromParent() {
        if (myPositionData == null) return
        myPositionData!!.remove()
    }

    @JsName("parentProperty")   // `parent` clashes with HasParent.parent
    fun parent(): DelayedValueProperty<ParentT> {
        return myParent
    }

    fun setPositionData(positionData: PositionData<out SiblingT>?) {
        myPositionData = positionData
    }
}