package jetbrains.livemap.core.multitasking.coroutine

import jetbrains.livemap.core.ecs.EcsComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext


class MicroCoThreadComponent(
) : EcsComponent, CoroutineContext.Element {

    internal lateinit var deferred: Deferred<*>
    var quantum: Int = 1
    val isCompleted get() = deferred.isCompleted

    override val key: CoroutineContext.Key<MicroCoThreadComponent> get() = Key
    companion object Key :
        CoroutineContext.Key<MicroCoThreadComponent>
}

var CoroutineScope.quantum: Int
    get() = coroutineContext.quantum
    set(value) { coroutineContext.quantum = value }

val CoroutineContext.microThread: MicroCoThreadComponent get() = get(MicroCoThreadComponent)!!

var CoroutineContext.quantum: Int
    get() = get(MicroCoThreadComponent)!!.quantum
    set(value) { get(MicroCoThreadComponent)!!.quantum = value }

