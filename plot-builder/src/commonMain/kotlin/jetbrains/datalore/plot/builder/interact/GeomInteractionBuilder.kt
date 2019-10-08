package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupStrategy

class GeomInteractionBuilder(private val mySupportedAesList: List<Aes<*>>) {
    lateinit var locatorLookupSpace: LookupSpace
        private set
    lateinit var locatorLookupStrategy: LookupStrategy
        private set
    private var myAxisTooltipVisibilityFromFunctionKind: Boolean = false
    private var myAxisTooltipVisibilityFromConfig: Boolean? = null
    private var myAxisAesFromFunctionKind: List<Aes<*>>? = null
    private var myAxisAesFromConfig: List<Aes<*>>? = null

    val axisAes: List<Aes<*>>
        get() = myAxisAesFromConfig ?: myAxisAesFromFunctionKind ?: ArrayList()

    val displayableAes: List<Aes<*>>
        get() = if (myAxisAesFromFunctionKind != null) {
            jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder.Companion.exclude(
                mySupportedAesList,
                myAxisAesFromFunctionKind!!
            )
        } else mySupportedAesList

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    fun showAxisTooltip(isTrue: Boolean): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        myAxisTooltipVisibilityFromConfig = isTrue
        return this
    }

    fun multilayerLookupStrategy(): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        locatorLookupStrategy = LookupStrategy.NEAREST
        locatorLookupSpace = LookupSpace.XY
        return this
    }

    fun univariateFunction(lookupStrategy: LookupStrategy): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        myAxisAesFromFunctionKind = jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder.Companion.AES_X
        locatorLookupStrategy = lookupStrategy
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.X

        return this
    }

    fun bivariateFunction(area: Boolean): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        myAxisAesFromFunctionKind = jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder.Companion.AES_XY

        if (area) {
            locatorLookupStrategy = LookupStrategy.HOVER
            myAxisTooltipVisibilityFromFunctionKind = false
        } else {
            locatorLookupStrategy = LookupStrategy.NEAREST
            myAxisTooltipVisibilityFromFunctionKind = true
        }
        locatorLookupSpace = LookupSpace.XY

        return this
    }

    fun none(): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        myAxisAesFromFunctionKind = ArrayList(mySupportedAesList)
        locatorLookupStrategy = LookupStrategy.NONE
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.NONE

        return this
    }

    fun build(): jetbrains.datalore.plot.builder.interact.GeomInteraction {
        return jetbrains.datalore.plot.builder.interact.GeomInteraction(this)
    }

    fun axisAes(axisAes: List<Aes<*>>): jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder {
        myAxisAesFromConfig = ArrayList(axisAes)
        return this
    }

    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)

        private fun exclude(list: List<Aes<*>>, itemsToRemove: List<Aes<*>>): List<Aes<*>> {
            val visibleAes = ArrayList(list)
            visibleAes.removeAll(itemsToRemove)

            return visibleAes
        }
    }
}
