package jetbrains.datalore.base.event



open class Event {
    var eventContext: EventContext? = null
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

    internal fun doConsume() {
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
