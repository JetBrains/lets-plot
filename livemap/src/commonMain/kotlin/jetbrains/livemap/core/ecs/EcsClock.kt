package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.SystemTime

interface EcsClock {
    val systemTime: SystemTime
    val updateStartTime: Long
}