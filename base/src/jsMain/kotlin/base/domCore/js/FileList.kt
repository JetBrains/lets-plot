package jetbrains.datalore.base.domCore.js

class FileList(private val fileList: org.w3c.files.FileList) {
    val length: Int
        get() = fileList.length

    fun item(index: Int): File? {
        return File.create(fileList.item(index))
    }
}
