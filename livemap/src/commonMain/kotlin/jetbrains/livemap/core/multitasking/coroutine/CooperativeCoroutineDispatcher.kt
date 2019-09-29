package jetbrains.livemap.core.multitasking.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

class CooperativeCoroutineDispatcher : CoroutineDispatcher() {
    private val continuations = HashMap<MicroCoThreadComponent, Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        check(!continuations.containsKey(context.microThread))
        continuations[context.microThread] = block
    }

    fun resume(task: MicroCoThreadComponent): Boolean {
        continuations
            .remove(task)
            .let { continuation ->
                return when (continuation) {
                    null -> false
                    else -> true.also{ continuation.run() }
                }
            }
    }
}
