/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration

import jetbrains.datalore.base.function.Supplier

class RegistrationMap<KeyT> {
    private val myMap = HashMap<KeyT, Registration>()

    fun put(key: KeyT, registration: Registration) {
        val prev = myMap.put(key, registration)
        if (prev != null) {
            prev.remove()
            myMap.remove(key)!!.remove()
            throw IllegalStateException("Registration for the key '$key' already exists.")
        }
    }

    fun replace(key: KeyT, registrationSupplier: Supplier<Registration>): Boolean {
        val res = removeOptional(key)
        myMap[key] = registrationSupplier.get()
        return res
    }

    fun remove(key: KeyT) {
        val prev = myMap.remove(key)
        if (prev != null) {
            prev.remove()
        } else {
            throw IllegalStateException("Registration for the key '$key' not found.")
        }
    }

    fun removeOptional(key: KeyT): Boolean {
        val prev = myMap.remove(key)
        if (prev != null) {
            prev.remove()
            return true
        } else {
            return false
        }
    }

    fun keys(): Set<KeyT> {
        return myMap.keys
    }

    fun clear() {
        try {
            for (r in myMap.values) {
                r.remove()
            }
        } finally {
            myMap.clear()
        }
    }
}