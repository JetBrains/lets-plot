package jetbrains.datalore.visualization.plot.server.config.transform

import jetbrains.datalore.visualization.plot.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.config.transform.SpecChangeContext

internal actual class ImageTranscodeSpecChange : SpecChange {
    override fun isApplicable(spec: Map<String, Any>): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        println("WARN: ImageTranscodeSpecChange is not implemented for native target")
        return false
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

