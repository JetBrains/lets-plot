/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.json.JsonSupport

@Suppress("UNCHECKED_CAST")
fun parsePlotSpec(spec: String) = spec.replace("'", "\"").let { JsonSupport.parseJson(it) as MutableMap<String, Any> }
