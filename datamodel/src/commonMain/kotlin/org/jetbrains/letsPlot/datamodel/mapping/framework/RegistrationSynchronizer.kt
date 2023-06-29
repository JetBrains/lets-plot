/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import jetbrains.datalore.base.registration.Registration

/**
 * Synchronizer which:
 * - create a registration on attach
 * - remove this registration on detach
 */
abstract class RegistrationSynchronizer : Synchronizer {
    private var myReg: Registration? = null

    override fun attach(ctx: SynchronizerContext) {
        myReg = doAttach(ctx)
    }

    protected abstract fun doAttach(ctx: SynchronizerContext): Registration

    override fun detach() {
        myReg!!.remove()
    }
}