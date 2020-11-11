/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import jetbrains.datalore.base.js.css.StyleMap
import org.w3c.dom.Window
import kotlinx.browser.document
import kotlinx.browser.window

class DomWindow(private val myWindow: Window) : DomEventTarget() {

    private var myImmediatePolyfill: DomImmediatePolyfill? = null

    val location: DomLocation
        get() = myWindow.location

    val scrollX: Double
        get() = myWindow.scrollX

    val scrollY: Double
        get() = myWindow.scrollY

    val innerWidth: Int
        get() = myWindow.innerWidth

    val innerHeight: Int
        get() = myWindow.innerHeight

    val devicePixelRatio: Double
        get() = myWindow.devicePixelRatio

    fun getComputedStyle(element: DomElement): StyleMap {
        return myWindow.getComputedStyle(element)
    }

    fun setImmediate(runnable: () -> Unit) {
        if (myImmediatePolyfill == null) {
            myImmediatePolyfill = DomImmediatePolyfill()
        }
        myImmediatePolyfill!!.setImmediate(runnable)
    }


    fun requestAnimationFrame(callback: (Double) -> Unit): Int {
        return myWindow.requestAnimationFrame(callback)
    }

    fun cancelAnimationFrame(requestId: Int) {
        myWindow.cancelAnimationFrame(requestId)
    }

    fun postMessage(message: String, targetOrigin: String) {
        myWindow.postMessage(message, targetOrigin)
    }

    fun scrollTo(x: Double, y: Double) {
        myWindow.scrollTo(x, y)
    }

    fun atob(data: String): String {
        return myWindow.atob(data)
    }

    fun btoa(str: String): String {
        return myWindow.btoa(str)
    }

    fun open(url: String, name: String, features: String): DomWindow? {
        val win = myWindow.open(url, name, features)
        return if (win != null) DomWindow(win) else null
    }

    companion object {
        private val domWindow = DomWindow(window)

        fun getWindow(): DomWindow {
            return domWindow
        }

        fun getDocument(): DomDocument {
            return document
        }

        fun getConsole(): DomConsole {
            return console
        }
    }
}
