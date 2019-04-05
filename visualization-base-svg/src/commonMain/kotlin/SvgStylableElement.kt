package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property
import java.util.ArrayList
import java.util.Arrays

abstract class SvgStylableElement : SvgElement() {

    fun classAttribute(): Property<String> {
        return getAttribute(CLASS)
    }

    fun addClass(@Nonnull cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        if (attr.get() == null) {
            attr.set(cl)
            return true
        }

        if (Arrays.asList(attr.get().split(" ")).contains(cl)) {
            return false
        }

        attr.set(attr.get() + " " + cl)
        return true
    }

    fun removeClass(@Nonnull cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        if (attr.get() == null) {
            return false
        }

        val classes = ArrayList(Arrays.asList(attr.get().split(" ")))
        val result = classes.remove(cl)

        if (result) {
            attr.set(buildClassString(classes))
        }

        return result
    }

    fun replaceClass(@Nonnull oldClass: String, @Nonnull newClass: String) {
        validateClassName(oldClass)
        validateClassName(newClass)

        val attr = classAttribute()
        if (attr.get() == null) {
            throw IllegalStateException("Trying to replace class when class is empty")
        }

        val classes = Arrays.asList(attr.get().split(" "))
        if (!classes.contains(oldClass)) {
            throw IllegalStateException("Class attribute does not contain specified oldClass")
        }

        classes.set(classes.indexOf(oldClass), newClass)

        attr.set(buildClassString(classes))
    }

    fun toggleClass(@Nonnull cl: String): Boolean {
        if (hasClass(cl)) {
            removeClass(cl)
            return false
        } else {
            addClass(cl)
            return true
        }
    }

    fun hasClass(@Nonnull cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        return attr.get() != null && Arrays.asList(attr.get().split(" ")).contains(cl)
    }

    fun fullClass(): String {
        val attr = classAttribute()
        return if (attr.get() == null) "" else attr.get()
    }

    private fun buildClassString(classes: List<String>): String {
        val builder = StringBuilder()
        for (className in classes) {
            if (builder.length > 0) {
                builder.append(' ')
            }
            builder.append(className)
        }
        return builder.toString()
    }

    private fun validateClassName(@Nonnull cl: String) {
        if (cl.contains(" ")) {
            throw IllegalArgumentException("Class name cannot contain spaces")
        }
    }

    companion object {
        private val CLASS = SvgAttributeSpec.createSpec("class")
    }
}
