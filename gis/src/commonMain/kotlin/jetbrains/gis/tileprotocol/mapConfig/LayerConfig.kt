package jetbrains.gis.tileprotocol.mapConfig

class LayerConfig {
    lateinit var name: String
    var border: Double = 0.0
    lateinit var columns: List<String>
    lateinit var table: String
    lateinit var rulesByTileSheet: Map<String, List<List<Rule>>>
    var order: String? = null

    fun tileSheets(): Set<String> {
        return rulesByTileSheet.keys
    }

    fun getRules(tileSheetName: String): List<List<Rule>> {
        return rulesByTileSheet.getOrElse(tileSheetName, { emptyList() })
    }
}