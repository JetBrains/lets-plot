package jetbrains.datalore.base.js.dom

import jetbrains.datalore.base.registration.Registration
import kotlin.random.Random

class DomImmediatePolyfill {

    private val myId: String = "set_immediate_" + Random.nextDouble()
    private val myQueue = ArrayList<() -> Unit>()
    private var myRegistration: Registration? = null

    private fun onMessage(event: DomMessageEvent) {
        val data = event.data
        if (myId == data && !myQueue.isEmpty()) {
            val head = myQueue.removeAt(0)
            head()
        }
    }

    fun setImmediate(runnable: () -> Unit) {
        if (myRegistration == null) {
            myRegistration = DomWindow.getWindow().on(DomEventType.MESSAGE, { value: DomMessageEvent ->
                onMessage(value)
            } as ((DomMessageEvent) -> Unit))
        }
        myQueue.add(runnable)
        DomWindow.getWindow().postMessage(myId, "*")
    }
}
