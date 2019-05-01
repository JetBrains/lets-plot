package jetbrains.datalore.base.observable.transform

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
