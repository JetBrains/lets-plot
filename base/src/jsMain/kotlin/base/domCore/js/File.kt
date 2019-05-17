package jetbrains.datalore.base.domCore.js

class File(private val file: org.w3c.files.File) {
    val name: String
        get() = file.name

    val size: Int
        get() = file.size

    companion object {
        fun create(file: org.w3c.files.File?): File? {
            return if (file != null) File(file) else null
        }
    }
}
