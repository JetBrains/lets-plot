/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event


open class Event {
    private var eventContext: EventContext? = null
        set(eventContext) {
            if (this.eventContext != null) {
                throw kotlin.IllegalStateException("Already set " + this.eventContext!!)
            }
            if (isConsumed) {
                throw IllegalStateException("Can't set a context to the consumed event")
            }
            if (eventContext == null) {
                throw IllegalArgumentException("Can't set null context")
            }
            field = eventContext
        }
    var isConsumed: Boolean = false
        private set

    fun consume() {
        doConsume()
    }

    private fun doConsume() {
        if (isConsumed) {
            throw IllegalStateException()
        }
        isConsumed = true
    }

    fun ensureConsumed() {
        if (!isConsumed) {
            consume()
        }
    }
}
