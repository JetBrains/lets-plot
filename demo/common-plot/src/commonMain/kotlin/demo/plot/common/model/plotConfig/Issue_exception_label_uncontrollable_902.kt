/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

/**
 * https://github.com/JetBrains/lets-plot/issues/902
 */
@Suppress("ClassName")
class Issue_exception_label_uncontrollable_902 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        val message =
            "Can't detect type of pattern 'Let's Talk About Sex (1998) Drama2235One Man's Hero (1999).movieId(mean)' used in string pattern '@{Let's Talk About Sex (1998) Drama2235One Man's Hero (1999).movieId(mean)}'"
        return listOf(
            failureSpecs(isInternal = false, message = "Not internal."),
            failureSpecs(isInternal = true, message = "Is internal."),
            failureSpecs(isInternal = false, message = message),
            failureSpecs(isInternal = true, message = message),
        )
    }

    private fun failureSpecs(
        isInternal: Boolean,
        message: String
    ): MutableMap<String, Any> {
        return mutableMapOf(
            "kind" to "error_gen",
            "is_internal" to isInternal,
            "message" to message
        )
    }
}