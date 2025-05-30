/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.commons.SystemTime

interface EcsClock {
    val systemTime: SystemTime
    val frameStartTimeMs: Long
    val frameDurationMs: Long
}