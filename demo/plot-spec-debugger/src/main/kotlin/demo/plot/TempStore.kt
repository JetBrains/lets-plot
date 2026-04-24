/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal class TempStore {
    data class Entry(
        val id: String,
        val title: String,
        val spec: String
    )

    private val entries = mutableListOf<Entry>()

    fun load() {
        val file = AppPaths.tempFile
        if (!file.exists()) return
        try {
            val text = file.readText()
            if (text.isBlank()) return
            val parsed = JsonSupport.parseJson(text) as? List<*> ?: return
            entries.clear()
            parsed.forEach { item ->
                val map = item as? Map<*, *> ?: return@forEach
                val id = map["id"] as? String ?: return@forEach
                val title = map["title"] as? String ?: return@forEach
                val spec = map["spec"] as? String ?: return@forEach
                entries += Entry(id, title, spec)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save() {
        val file = AppPaths.tempFile
        file.parentFile.mkdirs()
        file.writeText(
            JsonSupport.formatJson(
                entries.map { entry ->
                    mapOf(
                        "id" to entry.id,
                        "title" to entry.title,
                        "spec" to entry.spec
                    )
                }
            )
        )
    }

    operator fun get(id: String): Entry? = entries.firstOrNull { it.id == id }

    fun add(spec: String): Entry {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            title = "Temp ${LocalDateTime.now().format(TITLE_FORMAT)}",
            spec = spec
        )
        entries.add(0, entry)
        trimToLimit()
        return entry
    }

    fun update(id: String, spec: String): Entry? {
        val index = entries.indexOfFirst { it.id == id }
        if (index < 0) return null
        val current = entries[index]
        if (current.spec == spec) return current
        val updated = current.copy(spec = spec)
        entries[index] = updated
        return updated
    }

    fun remove(id: String) {
        entries.removeAll { it.id == id }
    }

    fun ids(): List<String> = entries.map { it.id }

    private fun trimToLimit() {
        while (entries.size > MAX_ENTRIES) {
            entries.removeLast()
        }
    }

    private companion object {
        const val MAX_ENTRIES = 20
        val TITLE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")
    }
}
