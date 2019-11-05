/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.socket

interface SocketHandler {
    fun onClose(message: String)
    fun onError(cause: Throwable)
    fun onTextMessage(message: String)
    fun onBinaryMessage(message: ByteArray)
    fun onOpen()
}