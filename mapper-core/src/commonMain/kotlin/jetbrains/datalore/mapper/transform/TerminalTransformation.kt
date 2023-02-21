/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.transform

import jetbrains.datalore.base.registration.Disposable

abstract class TerminalTransformation<ItemT> : Disposable {

    private var myDisposed: Boolean = false

    abstract val target: ItemT

    override fun dispose() {
        if (myDisposed) {
            throw IllegalStateException("Already disposed")
        }
        myDisposed = true
        doDispose()
    }

    protected open fun doDispose() {}
}
