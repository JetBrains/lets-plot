/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.util

import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ServiceLoaderHelper {
    private val classLoaders = ConcurrentHashMap.newKeySet<ClassLoader>()

    fun addClassLoader(classLoader: ClassLoader) {
        classLoaders.add(classLoader)
    }

    fun <T> loadInstances(serviceClass: Class<T>): List<T> {
        val allClassLoaders = buildSet {
            add(serviceClass.classLoader)
            addAll(classLoaders)
        }

        val loaders = allClassLoaders.map { classLoader ->
            ServiceLoader.load(serviceClass, classLoader)
        }

        return loaders.flatMap { it.toList() }.distinct()
    }

    inline fun <reified T> loadInstances(): List<T> {
        return loadInstances(T::class.java)
    }
}
