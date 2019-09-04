package jetbrains.gis.tileprotocol.socket

import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandler

class SafeSocketHandler(private val myHandler: SocketHandler, private val myThrowableHandler: ThrowableHandler) :
    SocketHandler {

    private fun safeRun(runnable: () -> Unit) {
        try {
            runnable()
        } catch (t: Throwable) {
            myThrowableHandler.handle(t)
        }
    }

    override fun onClose(message: String) {
        safeRun { myHandler.onClose(message) }
    }

    override fun onError(cause: Throwable) {
        safeRun { myHandler.onError(cause) }
    }

    override fun onTextMessage(message: String) {
        safeRun { myHandler.onTextMessage(message) }
    }

    override fun onBinaryMessage(message: ByteArray) {
        safeRun { myHandler.onBinaryMessage(message) }
    }

    override fun onOpen() {
        safeRun { myHandler.onOpen() }
    }
}
