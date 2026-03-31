package org.jetbrains.letsPlot.livemap.core.multitasking

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MicroTaskMultiThreadedExecutorFactory {
    actual companion object {
        actual fun create(): MicroTaskExecutor? {
            return null
        }
    }
}