package org.jetbrains.letsPlot.commons.formatting.string

object ByteSizeFormatter {
    private val teraFormat = StringFormat.of("{.2f} TB")
    private val gigaFormat = StringFormat.of("{.2f} GB")
    private val megaFormat = StringFormat.of("{.2f} MB")
    private val kiloFormat = StringFormat.of("{.2f} KB")

    private const val KILO = 1024.0
    private const val MEGA = KILO * KILO
    private const val GIGA = MEGA * KILO
    private const val TERA = GIGA * KILO

    fun formatByteSize(size: Number): String {
        val size = size.toDouble()

        return when {
            size >= TERA -> teraFormat.format(size / TERA)
            size >= GIGA -> gigaFormat.format(size / GIGA)
            size >= MEGA -> megaFormat.format(size / MEGA)
            size >= KILO -> kiloFormat.format(size / KILO)
            else -> "$size B"
        }
    }
}
