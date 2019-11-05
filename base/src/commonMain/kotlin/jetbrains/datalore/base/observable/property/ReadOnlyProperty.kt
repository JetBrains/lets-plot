/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

/**
 * Read only wrapper for a property
 */
class ReadOnlyProperty<ValueT>(private val myProperty: ReadableProperty<ValueT>) : BaseReadableProperty<ValueT>() {

    override val propExpr: String
        get() = "readOnly(" + myProperty.propExpr + ")"

    override fun get(): ValueT {
        return myProperty.get()
    }

    override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT>>): Registration {
        return myProperty.addHandler(handler)
    }
}