/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

interface MicroTask<ItemT> {
    fun resume()
    fun alive(): Boolean
    fun getResult(): ItemT
}

fun <ItemT, ResultT> MicroTask<ItemT>.map(success: (ItemT) -> ResultT): MicroTask<ResultT> {
    return MicroTaskUtil.map(this, success)
}

fun <ItemT, ResultT> MicroTask<ItemT>.flatMap(success: (ItemT) -> MicroTask<ResultT>): MicroTask<ResultT> {
    return MicroTaskUtil.flatMap(this, success)
}
