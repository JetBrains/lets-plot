package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property

abstract class SvgStylableElement : SvgElement() {

    companion object {
        private val CLASS: SvgAttributeSpec<String> = SvgAttributeSpec.createSpec("class")
    }

    fun classAttribute(): Property<String?> {
        return getAttribute(CLASS)
    }

    fun addClass(cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        if (attr.get() == null) {
            attr.set(cl)
            return true
        }

        if (attr.get()!!.split(" ").contains(cl)) {
            return false
        }

        attr.set(attr.get() + " " + cl)
        return true
    }

    fun removeClass(cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        if (attr.get() == null) {
            return false
        }

        val classes = ArrayList(attr.get()!!.split(" "))
        val result = classes.remove(cl)

        if (result) {
            attr.set(buildClassString(classes))
        }

        return result
    }

    fun replaceClass(oldClass: String, newClass: String) {
        validateClassName(oldClass)
        validateClassName(newClass)

        val attr = classAttribute()
        if (attr.get() == null) {
            throw IllegalStateException("Trying to replace class when class is empty")
        }

        val classes = attr.get()!!.split(" ")
        if (!classes.contains(oldClass)) {
            throw IllegalStateException("Class attribute does not contain specified oldClass")
        }

        val mutableClasses = MutableList(classes.size) { i -> classes[i] }
        mutableClasses[classes.indexOf(oldClass)] = newClass

        attr.set(buildClassString(classes))
    }

    fun toggleClass(cl: String): Boolean {
        if (hasClass(cl)) {
            removeClass(cl)
            return false
        } else {
            addClass(cl)
            return true
        }
    }

    fun hasClass(cl: String): Boolean {
        validateClassName(cl)

        val attr = classAttribute()
        return attr.get() != null && ArrayList(attr.get()!!.split(" ")).contains(cl)
    }

    fun fullClass(): String {
        val attr = classAttribute()
        return if (attr.get() == null) "" else attr.get()!!
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

    private fun validateClassName(cl: String) {
        if (cl.contains(" ")) {
            throw IllegalArgumentException("Class name cannot contain spaces")
        }
    }
}
