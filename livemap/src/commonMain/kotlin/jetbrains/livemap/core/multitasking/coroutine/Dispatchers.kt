package jetbrains.livemap.core.multitasking.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

lateinit var defaultCoroutineDispatcher: CoroutineDispatcher

fun <T> microCoThread(context: CoroutineContext = defaultCoroutineDispatcher, block: suspend CoroutineScope.() -> T): MicroCoThreadComponent {
    // Only CoroutineTaskDispatcher supports cooperative multitasking
    check(context[ContinuationInterceptor] is CooperativeCoroutineDispatcher)

    return MicroCoThreadComponent().apply {
        deferred = CoroutineScope(context + this).async(block = block)
    }
}
