/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration

/**
 * Registration which consists of several subregistrations.
 * Useful as an utility to aggregate registration and them dispose them with one call.
 */
class CompositeRegistration(vararg regs: Registration) : Registration() {
    private val myRegistrations: MutableList<Registration>

    val isEmpty: Boolean
        get() = myRegistrations.isEmpty()

    init {
        myRegistrations = arrayListOf(*regs)
    }

    fun add(r: Registration): CompositeRegistration {
        myRegistrations.add(r)
        return this
    }

    fun add(vararg rs: Registration): CompositeRegistration {
        for (r in rs) {
            add(r)
        }
        return this
    }

    override fun doRemove() {
        for (i in myRegistrations.size - 1 downTo -1 + 1) {
            myRegistrations[i].remove()
        }
        myRegistrations.clear()
    }
}