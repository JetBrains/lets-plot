/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.livemap.containers.PriorityQueue
import jetbrains.livemap.core.ecs.EcsSystem

class MetricsService (private val mySystemTime: SystemTime) {
    private val myMeasures = PriorityQueue(compareBy(Pair<EcsSystem, Double>::second).reversed())

    private var myBeginTime: Long = 0

    var totalUpdateTime = 0.0
        private set

    private val myValuesMap = HashMap<String, String>()
    private var myValuesOrder: List<String> = ArrayList()

    val values: Collection<String>
        get() = ArrayList<String>().apply {
            for (key in myValuesOrder) {
                myValuesMap[key]?.let { if (it.isNotEmpty()) this.add(it) }
            }
        }

    fun beginMeasureUpdate() {
        myBeginTime = mySystemTime.getTimeMs()
    }

    fun endMeasureUpdate(system: EcsSystem) {
        val time = mySystemTime.getTimeMs() - myBeginTime
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
        myValuesMap[key] = value
    }

    fun setValuesOrder(keys: List<String>) {
        myValuesOrder = keys
    }
}