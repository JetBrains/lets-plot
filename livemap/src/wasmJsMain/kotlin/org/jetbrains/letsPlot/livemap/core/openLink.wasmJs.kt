package org.jetbrains.letsPlot.livemap.core

import kotlinx.browser.window

actual fun openLink(href: String) {
    window.open(href)
}