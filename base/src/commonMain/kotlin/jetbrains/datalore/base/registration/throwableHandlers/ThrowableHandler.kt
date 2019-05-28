package jetbrains.datalore.base.registration.throwableHandlers

class ThrowableHandler internal constructor() {

    fun handle(t: Throwable) {
        throw t
    }
}
