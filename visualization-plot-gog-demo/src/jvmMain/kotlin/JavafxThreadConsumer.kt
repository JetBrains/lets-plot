package jetbrains.datalore.visualization.gogDemo

import javafx.application.Platform

class JavafxThreadConsumer<T>(val consumer: (T) -> Unit) {
    private val valueKeeper = ThreadSafeValueKeeper<T?>(null)

    fun accept(value: T) {
        if (valueKeeper.replace(value) == null) {
            Platform.runLater {
                consumer(valueKeeper.replace(null)!!)
            }
        }
    }

    private class ThreadSafeValueKeeper<T>(var value: T) {
        @Synchronized
        fun replace(value: T): T {
            val oldValue = this.value
            this.value = value
            return oldValue
        }
    }
}
