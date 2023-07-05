/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.registration.Registration

/// Default method implementation added due to this issue:
/// https://youtrack.jetbrains.com/issue/KT-48132
/// Can be removed for Kotlin >= 1.6.0
internal interface ResolvableAsync<ItemT> : Async<ItemT>, AsyncResolver<ItemT> {
    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration = Registration.EMPTY
    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration =
        Registration.EMPTY
}