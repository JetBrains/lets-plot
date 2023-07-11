/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.children

import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty

class ChildProperty<ParentT, ChildT : org.jetbrains.letsPlot.commons.intern.observable.children.SimpleComposite<in ParentT?, in ChildT>>(private val myParent: ParentT) :
    ValueProperty<ChildT?>(null) {

    override fun set(value: ChildT?) {
        if (get() === value) return

        if (value != null && value.parent().get() != null) {
            throw IllegalStateException()
        }

        val oldValue = get()
        if (oldValue != null) {
            oldValue.parent().set(null)
            oldValue.setPositionData(null)
        }
        if (value != null) {
            value.parent().set(myParent)
            value.setPositionData(object :
                org.jetbrains.letsPlot.commons.intern.observable.children.PositionData<ChildT> {
                override fun get(): org.jetbrains.letsPlot.commons.intern.observable.children.Position<ChildT> {
                    return object : org.jetbrains.letsPlot.commons.intern.observable.children.Position<ChildT> {

                        override val role: Any
                            get() = this@ChildProperty

                        override fun get(): ChildT? {
                            return this@ChildProperty.get()
                        }
                    }
                }

                override fun remove() {
                    set(null)
                }
            })
        }

        super.set(value)

        if (oldValue != null) {
            oldValue.parent().flush()
        }
        value?.parent()?.flush()
    }
}