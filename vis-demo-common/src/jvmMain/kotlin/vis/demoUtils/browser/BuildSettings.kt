package jetbrains.datalore.vis.demoUtils.browser

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class BuildSettings(
    val js_artifact_version: String
) {
    companion object {
        fun fromFile(pathName: String): BuildSettings {
            val content = File(pathName).readLines().joinToString("\n")
            val parser = Yaml(configuration = YamlConfiguration(strictMode = false))
            return parser.parse(BuildSettings.serializer(), content)
        }
    }

}

//fun main() {
//    val input = BuildSettings("1.1.1.1.1")
//
//    val result = Yaml.default.stringify(BuildSettings.serializer(), input)
//
//    println(result)
//}
//fun main() {
//    val input = """
//        js_artifact_version: 2.2.2.2
//        js_artifact_version_extra: 3.3.3
//    """.trimIndent()
//
//    val parser = Yaml(configuration = YamlConfiguration(strictMode = false))
//    val result = parser.parse(BuildSettings.serializer(), input)
//    println(result)
//}