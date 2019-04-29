package jetbrains.datalore.visualization.plot.gog.config.transform

interface SpecChangeContext {
    fun getSpecsAbsolute(vararg keys: String): List<Map<String, Any>>
}
