package jetbrains.livemap.core

import jetbrains.livemap.core.ecs.EcsSystem

class MetricsService {
    private val systemTime = SystemTime()
    private val myMeasures = PriorityQueue(compareBy(Pair<EcsSystem, Double>::second).reversed())

    private var myBeginTime: Long = 0

    var totalUpdateTime = 0.0
        private set

    private val myValues = HashMap<String, String>()
    private var myValuesOrder: List<String>? = null

    val values: Collection<String>
        get() {
            val strings = ArrayList<String>()
            for (key in myValuesOrder!!) {
                val value = myValues[key]
                if (value != null && !value.isEmpty()) {
                    strings.add(value)
                }
            }
            return strings
        }

    fun beginMeasureUpdate() {
        myBeginTime = systemTime.getTimeMs()
    }

    fun endMeasureUpdate(system: EcsSystem) {
        val time = systemTime.getTimeMs() - myBeginTime
        myMeasures.add(Pair(system, time.toDouble()))
        totalUpdateTime += time
    }

    fun reset() {
        myMeasures.clear()
        totalUpdateTime = 0.0
    }

    fun slowestSystem(): Pair<EcsSystem, Double>? {
        return myMeasures.peek()
    }

    fun setValue(key: String, value: String) {
        myValues[key] = value
    }

    fun setValuesOrder(keys: List<String>) {
        myValuesOrder = keys
    }
}