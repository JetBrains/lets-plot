package jetbrains.datalore.base.observable.event

import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.registration.Registration

/**
 * Utility class for implementing multi-way synchronizations. It prevents infinite recursive updates
 * by using an internal flag.
 */
class MultiWaySync {
    var isInSync = false
        private set

    fun <EventT> inSync(source: EventSource<EventT>): EventSource<EventT> {
        return object : EventSource<EventT> {
            override fun addHandler(handler: EventHandler<EventT>): Registration {
                return source.addHandler(object : EventHandler<EventT> {
                    override fun onEvent(event: EventT) {
                        sync(object : Runnable {
                            override fun run() {
                                handler.onEvent(event)
                            }
                        })
                    }
                })
            }
        }
    }

    private fun startSync() {
        if (isInSync) {
            throw IllegalStateException("Nested syncs aren't support")
        }
        isInSync = true
    }

    private fun finishSync() {
        if (!isInSync) {
            throw IllegalStateException("Not in sync")
        }

        isInSync = false
    }

    fun sync(action: Runnable) {
        if (isInSync) return

        startSync()
        try {
            action.run()
        } finally {
            finishSync()
        }
    }
}