/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import org.jetbrains.letsPlot.base.intern.async.Async

interface GeoTransport {
    fun send(request: GeoRequest): Async<GeoResponse>
}
