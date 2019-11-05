/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

/**
 * Derived property which fires change events only after explicit update() call.
 * Used to wrap with interface properties which we don't have observable interface to.
 * In such cases the update() method is called by timer.
 */
abstract class UpdatableProperty<ValueT> protected constructor() :
        BaseDerivedProperty<ValueT?>(null) {
//        BaseDerivedProperty<ValueT>() {

    override val propExpr: String
        get() = "updatable property"

    fun update() {
        somethingChanged()
    }
}