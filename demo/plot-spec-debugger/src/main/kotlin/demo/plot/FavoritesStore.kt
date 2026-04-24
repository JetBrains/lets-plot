/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

internal class FavoritesStore {
    private val favorites = linkedMapOf<String, String>()

    fun load() {
        val file = AppPaths.favoritesFile
        if (!file.exists()) return
        try {
            val text = file.readText()
            if (text.isBlank()) return
            val parsed = JsonSupport.parseJson(text) as Map<*, *>
            favorites.clear()
            parsed.forEach { (key, value) ->
                if (key is String && value is String) favorites[key] = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save() {
        val file = AppPaths.favoritesFile
        file.parentFile.mkdirs()
        file.writeText(JsonSupport.formatJson(favorites))
    }

    operator fun get(name: String): String? = favorites[name]
    operator fun set(name: String, spec: String) { favorites[name] = spec }
    operator fun contains(name: String): Boolean = name in favorites
    fun remove(name: String) { favorites.remove(name) }
    fun sortedNames(): List<String> = favorites.keys.sorted()
}
