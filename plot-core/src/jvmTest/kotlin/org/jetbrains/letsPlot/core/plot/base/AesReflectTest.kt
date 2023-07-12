/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class AesReflectTest {
    @Test
    fun checkName() {
        val aesType = org.jetbrains.letsPlot.core.plot.base.Aes::class.createType(listOf(KTypeProjection.STAR))
        val companion = org.jetbrains.letsPlot.core.plot.base.Aes::class.companionObject!!
        val properties = companion.declaredMemberProperties
        var count = 0
        for (p in properties) {
            val pType = p.returnType
            if (pType.isSubtypeOf(aesType)) {
                count++
                @Suppress("UNCHECKED_CAST")
                val p1 = p as KProperty1<org.jetbrains.letsPlot.core.plot.base.Aes.Companion, org.jetbrains.letsPlot.core.plot.base.Aes<*>>
                val value = p1.get(org.jetbrains.letsPlot.core.plot.base.Aes)

                // check that the name passed to constructor corresponds to the field name
                assertEquals(p.name.lowercase(), value.name)
            }
        }

        assertEquals(count, org.jetbrains.letsPlot.core.plot.base.Aes.values().size)
    }
}