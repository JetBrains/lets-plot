/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package messages

import MessageHandler
import kotlinx.browser.window
import org.w3c.dom.*

internal class OverlayMessageHandler(
    private val plotContainer: HTMLElement,
) : MessageHandler {
    private var mute: Boolean = false

    override fun showError(message: String) {
        showText(message, "lets-plot-message-error", "color:darkred;")
    }

    override fun showComputationMessages(messages: List<String>) {
        if (mute || messages.isEmpty()) return

        val doc = plotContainer.ownerDocument!!

        // Create message counter button
        val counterButton = doc.createElement("button") as HTMLButtonElement
        counterButton.textContent = "${messages.size}"
        counterButton.className = "lets-plot-message-counter"
        counterButton.style.apply {
            position = "absolute"
            right = "4px"
            top = "4px"
            borderRadius = "8px"
            backgroundColor = "#4572E8"
            color = "white"
            border = "none"
            width = "28px"
            height = "16px"
            cursor = "pointer"
            fontSize = "10px"
            padding = "0"
            fontFamily = "monospace"
        }

        // Create overlay container
        val overlayDiv = doc.createElement("div") as HTMLDivElement
        overlayDiv.className = "lets-plot-message-overlay"
        overlayDiv.style.apply {
            display = "none"
            position = "absolute"
            right = "4px"
            top = "24px"
            backgroundColor = "white"
            border = "1px solid #ddd"
            borderRadius = "4px"
            padding = "10px"
            boxShadow = "0 2px 5px rgba(0,0,0,0.2)"
            zIndex = "1000"
            maxWidth = "calc(100% - 8px)"
            maxHeight = "calc(100% - 28px)"
            boxSizing = "border-box"  // Include the padding and border in dimensions.
            overflowX = "auto"
            overflowY = "auto"
        }

        // Toggle overlay visibility on button click
        counterButton.onclick = {
            val isVisible = overlayDiv.style.display == "block"
            if (isVisible) {
                overlayDiv.style.display = "none"
            } else {
                overlayDiv.style.display = "block"
            }
        }

        // Create the "copy & clear" button
        val copyAndClearButton = doc.createElement("a") as HTMLAnchorElement
        overlayDiv.appendChild(copyAndClearButton)
        copyAndClearButton.textContent = "copy & clear"
        copyAndClearButton.href = "#" // Prevent default behavior
        copyAndClearButton.style.apply {
            display = "block"  // To maintain block layout
            marginBottom = "8px"
            color = "#4572E8"
            fontSize = "10px"
            fontFamily = "monospace"
            textAlign = "left"
            textDecoration = "none"
        }

        // Add click handler
        copyAndClearButton.onclick = {
            // Copy text content to clipboard
            val textContent = messages.joinToString("\n")
            window.navigator.clipboard.writeText(textContent).then {
                overlayDiv.remove()
                counterButton.remove()
            }.catch { error ->
                console.error("Failed to copy text: ", error)
            }
        }

        // Add messages to overlay
        messages.forEach { message ->
            val messageElement = doc.createElement("p") as HTMLParagraphElement
            messageElement.className = "lets-plot-message-info"
            messageElement.style.apply {
                color = "darkblue"
                margin = "5px 0"
                whiteSpace = "nowrap"    // prevents text wrapping
            }
            messageElement.textContent = message
            overlayDiv.appendChild(messageElement)
        }

        plotContainer.appendChild(counterButton)
        plotContainer.appendChild(overlayDiv)
    }

    private fun showText(message: String, className: String, style: String) {
        val paragraphElement = plotContainer.ownerDocument!!.createElement("p") as HTMLParagraphElement

        if (style.isNotBlank()) {
            paragraphElement.setAttribute("style", style)
        }
        paragraphElement.textContent = message
        paragraphElement.className = className
        plotContainer.appendChild(paragraphElement)
    }

    override fun toMute(): OverlayMessageHandler {
        return OverlayMessageHandler(plotContainer).also { it.mute = true }
    }
}