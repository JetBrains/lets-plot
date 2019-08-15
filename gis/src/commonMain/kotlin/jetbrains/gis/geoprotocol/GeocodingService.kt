package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.AmbiguousFeature
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


class GeocodingService(private val myTransport: GeoTransport) {

    fun execute(request: GeoRequest): Async<List<GeocodedFeature>> {
        val requestedStrings: List<String>
        requestedStrings = when (request) {
            is ExplicitSearchRequest -> request.ids
            is GeocodingSearchRequest -> request.queries.flatMap { regionQuery -> regionQuery.names }
            is ReverseGeocodingSearchRequest -> emptyList()
            else -> return Asyncs.failure(IllegalStateException("Unkown request type: $request"))
        }

        val duplicateStorage = DuplicateStorage(requestedStrings)

        return myTransport
            .send(request)
            .map { response ->
                when (response) {
                    is SuccessGeoResponse -> duplicateStorage.restoreDuplicateList(response.features, GeocodedFeature::request)
                    is AmbiguousGeoResponse -> throw RuntimeException(createAmbiguousMessage(response.features))
                    is ErrorGeoResponse -> throw RuntimeException("GIS error: " + response.message)
                    else -> throw IllegalStateException("Unknown response status: $response")
                }
            }
    }

    internal class DuplicateStorage(objectKeys: List<String>) {
        private val myKeys: List<String>
        private val uniqueObjectKeys: MutableSet<String> = LinkedHashSet()

        init {
            objectKeys.forEach { uniqueObjectKeys.add(it) }
            myKeys = unmodifiableList<String>(objectKeys)
        }

        fun <T> restoreDuplicateList(uniqueObjectList: List<T>, getObjectKey: (T) -> String): List<T> {
            val uniqueObjectMap = HashMap<String, T>()
            uniqueObjectList.forEach { uniqueObjectMap[getObjectKey(it)] = it }

            val duplicateObjectList = ArrayList<T>()
            if (myKeys.isEmpty()) {
                duplicateObjectList.addAll(uniqueObjectList)
            } else {
                myKeys.forEach { key ->
                    if (uniqueObjectMap.containsKey(key)) {
                        uniqueObjectMap[key]?.let(duplicateObjectList::add)
                    }
                }
            }
            return duplicateObjectList
        }
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
                            .append("were found for '${ambiguousFeature.request}'")
                            .append(if (ambiguousFeature.namesakes.isEmpty()) "." else ":")

                        ambiguousFeature.namesakes
                            .map { (name, parents) -> "- ${name}${parents.joinToString(prefix = " (", transform = { it.name }, postfix = ")")}"}
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
