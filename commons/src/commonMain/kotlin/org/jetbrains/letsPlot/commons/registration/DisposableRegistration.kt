/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.registration

class DisposableRegistration(
    private val disposable: Disposable
) : Registration() {
    override fun doRemove() {
        disposable.dispose()
    }
}