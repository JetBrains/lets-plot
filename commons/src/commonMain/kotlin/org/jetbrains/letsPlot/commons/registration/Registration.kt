/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.registration

abstract class Registration : Disposable {

    private var isRemoved: Boolean = false

    final override fun dispose() = remove()

    fun remove() {
        setRemoved()
        doRemove()
    }

    protected open fun setRemoved() {
        check(!isRemoved) { "Registration already removed" }
        isRemoved = true
    }

    protected abstract fun doRemove()

    private class EmptyRegistration : Registration() {

        override fun doRemove() {}

        override fun setRemoved() {}
    }

    companion object {
        val EMPTY: Registration = EmptyRegistration()

        fun onRemove(code: () -> Unit): Registration {
            return object : Registration() {
                override fun doRemove() {
                    code()
                }
            }
        }

        fun from(disposable: Disposable): Registration {
            return object : Registration() {
                override fun doRemove() {
                    disposable.dispose()
                }
            }
        }

        fun from(vararg disposables: Disposable): Registration {
            return object : Registration() {
                override fun doRemove() {
                    for (d in disposables) {
                        d.dispose()
                    }
                }
            }
        }
    }
}