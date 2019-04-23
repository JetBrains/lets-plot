package jetbrains.datalore.mapper.core

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