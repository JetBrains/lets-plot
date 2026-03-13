/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

internal interface MessageHandler {
    fun showError(message: String)

    fun showComputationMessages(messages: List<String>)

    fun toMute(): MessageHandler
}