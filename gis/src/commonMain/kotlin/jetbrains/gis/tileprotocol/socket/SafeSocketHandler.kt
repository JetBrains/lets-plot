/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.socket

class SafeSocketHandler(private val myHandler: SocketHandler) : SocketHandler {

    private fun safeRun(runnable: () -> Unit) {
        try {
            runnable()
        } catch (t: Throwable) {
            // ToDo: this makes little sense
            throw t
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
