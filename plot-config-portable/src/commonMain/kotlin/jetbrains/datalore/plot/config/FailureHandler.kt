package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Throwables

object FailureHandler {
    fun failureInfo(e: Throwable): FailureInfo {
        @Suppress("NAME_SHADOWING")
        val e = Throwables.getRootCause(e)
        return if (!e.message.isNullOrBlank() && (
                    e is IllegalStateException ||
                            e is IllegalArgumentException)
        ) {
            // Not a bug - likely user configuration error like `No layers in plot`
            FailureInfo(e.message!!, false)
        } else {
            val className = e::class.simpleName ?: "<Anonymous exception>"
            FailureInfo(
                "Internal error occurred in datalore plot: $className : ${e.message ?: "<no message>"}",
                true
            )
        }
    }

    class FailureInfo(val message: String, val isInternalError: Boolean)
}