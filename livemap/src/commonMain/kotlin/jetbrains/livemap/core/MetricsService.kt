package jetbrains.livemap.core

import jetbrains.livemap.core.ecs.EcsSystem

class MetricsService {
    private val systemTime = SystemTime()
    private val measures = PriorityQueue(compareBy(Pair<EcsSystem, Double>::second).reversed())

    private var beginTime: Long = 0

    var totalUpdateTime = 0.0
        private set

    private val valuesMap = HashMap<String, String>()
    private var valuesOrder: List<String> = ArrayList()

    val values: Collection<String>
        get() = ArrayList<String>().apply {
            for (key in valuesOrder) {
                valuesMap[key]?.let { if (it.isNotEmpty()) this.add(it) }
            }
        }

    fun beginMeasureUpdate() {
        beginTime = systemTime.getTimeMs()
    }

    fun endMeasureUpdate(system: EcsSystem) {
        val time = systemTime.getTimeMs() - beginTime
        measures.add(Pair(system, time.toDouble()))
        totalUpdateTime += time
    }

    fun reset() {
        measures.clear()
        totalUpdateTime = 0.0
    }

    fun slowestSystem(): Pair<EcsSystem, Double>? {
        return measures.peek()
    }

    fun setValue(key: String, value: String) {
        valuesMap[key] = value
    }

    fun setValuesOrder(keys: List<String>) {
        valuesOrder = keys
    }
}