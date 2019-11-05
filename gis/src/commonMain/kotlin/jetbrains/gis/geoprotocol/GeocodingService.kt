/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.AmbiguousFeature
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


class GeocodingService(private val myTransport: GeoTransport) {

    fun execute(request: GeoRequest): Async<List<GeocodedFeature>> {
        val queries: List<String> = when (request) {
            is ExplicitSearchRequest -> request.ids
            is GeocodingSearchRequest -> request.queries.flatMap { regionQuery -> regionQuery.names }
            is ReverseGeocodingSearchRequest -> emptyList()
            else -> return Asyncs.failure(IllegalStateException("Unknown request type: $request"))
        }

        val featureSelector: (SuccessGeoResponse) -> List<GeocodedFeature> =
            if (queries.isEmpty()) {
                { response -> response.features }
            } else {
                { response -> queries.leftJoin(response.features) { query, feature -> feature.request == query } }
            }

        return myTransport
            .send(request)
            .map { response ->
                when (response) {
                    is SuccessGeoResponse -> featureSelector(response)
                    is AmbiguousGeoResponse -> throw RuntimeException(createAmbiguousMessage(response.features))
                    is ErrorGeoResponse -> throw RuntimeException("GIS error: " + response.message)
                    else -> throw IllegalStateException("Unknown response status: $response")
                }
            }

    }

    private fun <T, U : Any> List<T>.leftJoin(collection: List<U>, filter: (T, U) -> Boolean): List<U> {
        return mapNotNull { key -> collection.firstOrNull { obj -> filter(key, obj) } }
    }

    companion object {
        internal fun createAmbiguousMessage(ambiguousFeatures: List<AmbiguousFeature>): String {
            val message = StringBuilder().append("Geocoding errors:\n")
            ambiguousFeatures.forEach { ambiguousFeature ->
                when {
                    ambiguousFeature.namesakeCount == 1 -> {}
                    ambiguousFeature.namesakeCount > 1 -> {
                        message
                            .append("Multiple objects (${ambiguousFeature.namesakeCount}")
                            .append(") were found for '${ambiguousFeature.request}'")
                            .append(if (ambiguousFeature.namesakes.isEmpty()) "." else ":")

                        ambiguousFeature.namesakes
                            .map { (name, parents) -> "- ${name}${parents.joinToString(prefix = "(", transform = { it.name }, postfix = ")")}"}
                            .sorted()
                            .forEach { message.append("\n$it") }

                    }
                    else -> message.append("No objects were found for '${ambiguousFeature.request}'.")
                }
                message.append("\n")
            }

            return message.toString()
        }
    }
}

