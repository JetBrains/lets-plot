package jetbrains.datalore.vis.swing

interface BatikMessageCallback {
    fun handleMessage(message: String)
    fun handleException(e: Exception)
}