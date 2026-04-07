/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom

import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.test.Test
import kotlin.test.assertEquals

class DomCanvasViewDetachTest {
    @Test
    fun detachedNodeShouldTriggerDisposeOnObservedMutation() {
        val mutationSource = TestMutationSource()
        var isConnected = true
        var detachCount = 0

        DomDetachObserver.onDetach(
            isConnected = { isConnected },
            onMutationObserved = mutationSource::observe,
            onDetached = { detachCount++ }
        )

        mutationSource.fire()
        assertEquals(0, detachCount)

        isConnected = false
        mutationSource.fire()
        assertEquals(1, detachCount)

        mutationSource.fire()
        assertEquals(1, detachCount)
    }

    @Test
    fun disposedRegistrationShouldStopMutationObservation() {
        val mutationSource = TestMutationSource()
        var detachCount = 0

        val registration = DomDetachObserver.onDetach(
            isConnected = { true },
            onMutationObserved = mutationSource::observe,
            onDetached = { detachCount++ }
        )

        registration.dispose()
        mutationSource.fire()

        assertEquals(0, detachCount)
    }

    private class TestMutationSource {
        private val callbacks = LinkedHashMap<Int, () -> Unit>()
        private var nextId = 1

        fun observe(callback: () -> Unit): Registration {
            val id = nextId++
            callbacks[id] = callback
            return Registration.onRemove {
                callbacks.remove(id)
            }
        }

        fun fire() {
            callbacks.values.toList().forEach { callback ->
                callback()
            }
        }
    }
}