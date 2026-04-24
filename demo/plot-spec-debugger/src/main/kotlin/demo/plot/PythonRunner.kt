/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

internal object PythonRunner {
    /**
     * returns Pair(JsonString?, ErrorMessage?)
     */
    fun runPythonScript(userCode: String, pythonPath: String): Pair<String?, String?> {
        // Strip LetsPlot.setup_html() from user code; for terminal use we force offline mode
        // with no JS in the wrapper to avoid network calls.
        val sanitizedUserCode = userCode.replace(Regex("""LetsPlot\.setup_html\([^)]*\)\s*"""), "")

        val wrapperScript = """
            |import sys
            |import json
            |import os
            |
            |try:
            |    from lets_plot import *
            |    from lets_plot._type_utils import standardize_dict
            |    LetsPlot.setup_html(offline=True, no_js=True)
            |except ImportError:
            |    print("Error: 'lets-plot' library is not installed in this Python environment.", file=sys.stderr)
            |    sys.exit(1)
            |
            |def run_user_code():
            |    # User code will be injected here via execution
            |    user_code_path = sys.argv[1]
            |
            |    with open(user_code_path, 'r', encoding='utf-8') as f:
            |        code = f.read()
            |
            |    local_scope = {}
            |
            |    try:
            |        lp_init = "from lets_plot import *; LetsPlot.setup_html(offline=True, no_js=True)\n"
            |        exec(lp_init + code, {}, local_scope)
            |    except Exception as e:
            |        import traceback
            |        traceback.print_exc(file=sys.stderr)
            |        sys.exit(1)
            |
            |    # Strategy:
            |    # 1. Look for variable named 'p' (convention)
            |    # 2. Look for any variable that has 'as_dict' method (lets-plot object)
            |
            |    plot_obj = local_scope.get('p')
            |
            |    if plot_obj is None:
            |        # Fallback: find the last defined object that looks like a plot
            |        candidates = [v for k, v in local_scope.items() if hasattr(v, 'as_dict')]
            |        if candidates:
            |            plot_obj = candidates[-1]
            |
            |    if plot_obj and hasattr(plot_obj, 'as_dict'):
            |        plot_dict = standardize_dict(plot_obj.as_dict())
            |        plot_json = json.dumps(plot_dict, indent=2)
            |        print(plot_json)
            |    else:
            |        print("Error: No plot object found. Please assign your plot to a variable named 'p'.", file=sys.stderr)
            |        sys.exit(1)
            |
            |if __name__ == "__main__":
            |    run_user_code()
            |""".trimMargin()

        try {
            val wrapperFile = File.createTempFile("lets_plot_wrapper", ".py")
            wrapperFile.writeText(wrapperScript)

            val userCodeFile = File.createTempFile("user_code", ".py")
            userCodeFile.writeText(sanitizedUserCode)

            val pb = ProcessBuilder(pythonPath, wrapperFile.absolutePath, userCodeFile.absolutePath)
            pb.environment()["PYTHONIOENCODING"] = "utf-8"

            val process = pb.start()

            val output = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            val errors = process.errorStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

            val exited = process.waitFor(10, TimeUnit.SECONDS)

            wrapperFile.delete()
            userCodeFile.delete()

            if (!exited) {
                process.destroy()
                return Pair(null, "Timeout: Python script took too long to execute.")
            }

            if (process.exitValue() != 0) {
                return Pair(null, errors.ifBlank { "Unknown Python error (Exit code ${process.exitValue()})" })
            }

            return Pair(output, if (errors.isNotBlank()) "Python stderr:\n$errors" else null)

        } catch (e: Exception) {
            return Pair(null, "Execution failed: ${e.message}")
        }
    }
}
