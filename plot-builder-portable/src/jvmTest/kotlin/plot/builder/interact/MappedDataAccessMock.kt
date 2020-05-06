/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.MappedDataAccess.MappedData
import jetbrains.datalore.plot.builder.interact.mockito.eq
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class MappedDataAccessMock {

    private val mappedAes = HashSet<Aes<*>>()
    val mappedDataAccess: MappedDataAccess = mock(MappedDataAccess::class.java)

    init {
        `when`(mappedDataAccess.mappedAes)
                .thenReturn(getMappedAes())
    }

    fun <T> add(mapping: Mapping<T>): MappedDataAccessMock {
        return add(mapping, null)
    }

    fun <T> add(mapping: Mapping<T>, index: Int?): MappedDataAccessMock {
        val aes = mapping.aes

        if (index == null) {
            `when`(mappedDataAccess.getMappedData(eq(aes), anyInt()))
                    .thenReturn(mapping.createMappedData())
        } else {
            `when`(mappedDataAccess.getMappedData(eq(aes), eq(index)))
                    .thenReturn(mapping.createMappedData())
        }

        `when`(mappedDataAccess.isMapped(eq(aes)))
                .thenReturn(true)

        getMappedAes().add(aes)

        return this
    }

    internal fun remove(aes: Aes<*>) {
        getMappedAes().remove(aes)

        `when`<MappedData<*>>(mappedDataAccess.getMappedData(eq(aes), anyInt()))
                .thenReturn(null)

        `when`(mappedDataAccess.isMapped(eq(aes)))
                .thenReturn(false)
    }

    fun getMappedAes(): MutableSet<Aes<*>> {
        return mappedAes
    }

    class Mapping<T> internal constructor(internal val aes: Aes<T>,
                                          private val label: String,
                                          private val value: String,
                                          private val isContinuous: Boolean) {
        fun longTooltipText(): String {
            return "$label: $value"
        }

        fun shortTooltipText(): String {
            return value
        }

        internal fun createMappedData(): MappedData<T> {
            return MappedData(label, value, isContinuous)
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
            return Mapping(aes, name, value, isContinuous)
        }

    }

    companion object {

        fun variable(): Variable {
            return Variable()
        }
    }
}
