/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.builder.interact.mockito.eq
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class MappedDataAccessMock {

    private val mappedAes = HashSet<Aes<*>>()
    val mappedDataAccess: MappedDataAccess = mock(MappedDataAccess::class.java)

    fun <T> add(mapping: Mapping<T>): MappedDataAccessMock {
        return add(mapping, null)
    }

    fun <T> add(mapping: Mapping<T>, index: Int?): MappedDataAccessMock {
        val aes = mapping.aes

        if (index == null) {
            `when`(mappedDataAccess.getMappedDataValue(eq(aes), anyInt()))
                .thenReturn(mapping.value)
        } else {
            `when`(mappedDataAccess.getMappedDataValue(eq(aes), eq(index)))
                .thenReturn(mapping.value)
        }

        `when`(mappedDataAccess.isMapped(eq(aes)))
                .thenReturn(true)
        `when`(mappedDataAccess.getMappedDataLabel(eq(aes)))
            .thenReturn(mapping.label)

        getMappedAes().add(aes)

        return this
    }

    fun getMappedAes(): MutableSet<Aes<*>> {
        return mappedAes
    }

    class Mapping<T> internal constructor(
        internal val aes: Aes<T>,
        internal val label: String,
        internal val value: String
    ) {
        fun longTooltipText(): String {
            return "$label: $value"
        }

        fun shortTooltipText(): String {
            return value
        }
    }

    class Variable {
        private var name = ""
        private var value = ""
        private var isContinuous: Boolean = false

        fun name(v: String): Variable {
            this.name = v
            return this
        }

        fun value(v: String): Variable {
            this.value = v
            return this
        }

        fun isContinuous(v: Boolean): Variable {
            this.isContinuous = v
            return this
        }

        fun <T> mapping(aes: Aes<T>): Mapping<T> {
            return Mapping(aes, name, value)
        }

    }

    companion object {

        fun variable(): Variable {
            return Variable()
        }
    }
}
