/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package messages

import MessageHandler
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement

internal class SimpleMessageHandler(
    private val messagesDiv: HTMLElement,
) : MessageHandler {
    private var mute: Boolean = false

    override fun showError(message: String) {
        showText(message, "lets-plot-message-error", "color:darkred;")
    }

    override fun showComputationMessages(messages: List<String>) {
        if (mute) return

        messages.forEach {
            showText(it, "lets-plot-message-info", "color:darkblue;")
        }
    }

    private fun showText(message: String, className: String, style: String) {
        val paragraphElement = messagesDiv.ownerDocument!!.createElement("p") as HTMLParagraphElement

        if (style.isNotBlank()) {
            paragraphElement.setAttribute("style", style)
        }
        paragraphElement.textContent = message
        paragraphElement.className = className
        messagesDiv.appendChild(paragraphElement)
    }

    override fun toMute(): SimpleMessageHandler {
        return SimpleMessageHandler(messagesDiv).also { it.mute = true }
    }
}