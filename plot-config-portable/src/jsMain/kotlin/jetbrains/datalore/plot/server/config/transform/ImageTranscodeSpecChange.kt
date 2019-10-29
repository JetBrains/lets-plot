package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext

internal actual class ImageTranscodeSpecChange : SpecChange {
    override fun isApplicable(spec: Map<String, Any>): Boolean {
        UNSUPPORTED("ImageTranscodeSpecChange")
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        UNSUPPORTED("ImageTranscodeSpecChange")
    }
}

