package jetbrains.datalore.base.gcommon.base

object Throwables {
    fun getRootCause(throwable: Throwable): Throwable {
        // Keep a second pointer that slowly walks the causal chain. If the fast pointer ever catches
        // the slower pointer, then there's a loop.
        var slowPointer: Throwable = throwable
        var advanceSlowPointer = false

        var cause = throwable
        while (cause.cause != null) {
            cause = cause.cause!!

            if (cause === slowPointer) {
                throw IllegalArgumentException("Loop in causal chain detected.", cause)
            }
            if (advanceSlowPointer) {
                slowPointer = slowPointer.cause!!
            }
            advanceSlowPointer = !advanceSlowPointer // only advance every other iteration
        }
        return cause
    }
}
