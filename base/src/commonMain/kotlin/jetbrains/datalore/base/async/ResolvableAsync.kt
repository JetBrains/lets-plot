/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.registration.Registration

internal interface ResolvableAsync<ItemT> : Async<ItemT>, AsyncResolver<ItemT>
{
    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration = Registration.EMPTY
//
    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration = Registration.EMPTY
}