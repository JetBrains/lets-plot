package jetbrains.datalore.visualization.plot

import jetbrains.datalore.base.json.JsonSupport

fun parsePlotSpec(spec: String) = JsonSupport.parseJson(spec) as MutableMap<String, Any>
