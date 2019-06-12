package jetbrains.datalore.visualization.base.swing

interface BatikMessageCallback {
    fun handleMessage(message: String)
    fun handleException(e: Exception)
}