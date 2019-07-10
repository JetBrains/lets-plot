package jetbrains.gis.tileprotocol.socket

import jetbrains.datalore.base.function.Functions.runnableOf
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.registration.throwableHandlers.ThrowableHandler

class SafeSocketHandler(private val myHandler: SocketHandler, private val myThrowableHandler: ThrowableHandler) :
    SocketHandler {

    private fun safeRun(runnable: Runnable) {
        try {
            runnable.run()
        } catch (t: Throwable) {
            myThrowableHandler.handle(t)
        }

    }

    override fun onClose() {
        safeRun( runnableOf(myHandler::onClose))
    }

    override fun onError(cause: Throwable) {
        safeRun(runnableOf { myHandler.onError(cause) })
    }

    override fun onTextMessage(message: String) {
        safeRun(runnableOf { myHandler.onTextMessage(message) })
    }

    override fun onBinaryMessage(message: ByteArray) {
        safeRun(runnableOf{ myHandler.onBinaryMessage(message) })
    }

    override fun onOpen() {
        safeRun(runnableOf { myHandler::onOpen })
    }
}
