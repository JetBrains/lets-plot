package jetbrains.datalore.visualization.plot.builder.data.sampling

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.Sampling
import jetbrains.datalore.visualization.plot.builder.data.PointSampling
import jetbrains.datalore.visualization.plot.builder.data.sampling.VertexSampling.VertexDpSampling
import jetbrains.datalore.visualization.plot.builder.data.sampling.VertexSampling.VertexVwSampling

object Samplings {
    const val RANDOM = RandomSampling.ALIAS
    const val PICK = PickSampling.ALIAS
    const val SYSTEMATIC = SystematicSampling.ALIAS
    const val RANDOM_GROUP = GroupRandomSampling.ALIAS
    const val SYSTEMATIC_GROUP = GroupSystematicSampling.ALIAS
    const val RANDOM_STRATIFIED = RandomStratifiedSampling.ALIAS
    const val VERTEX_VW = VertexVwSampling.ALIAS
    const val VERTEX_DP = VertexDpSampling.ALIAS

    val NONE: PointSampling = NoneSampling()

    fun random(sampleSize: Int, seed: Long?): PointSampling {
        return RandomSampling(sampleSize, seed)
    }

    fun pick(sampleSize: Int): PointSampling {
        return PickSampling(sampleSize)
    }

    fun vertexDp(sampleSize: Int): Sampling {
        return VertexDpSampling(sampleSize)
    }

    fun vertexVw(sampleSize: Int): Sampling {
        return VertexVwSampling(sampleSize)
    }

    fun systematic(sampleSize: Int): Sampling {
        return SystematicSampling(sampleSize)
    }

    fun randomGroup(sampleSize: Int, seed: Long?): Sampling {
        return GroupRandomSampling(sampleSize, seed)
    }

    fun systematicGroup(sampleSize: Int): Sampling {
        return GroupSystematicSampling(sampleSize)
    }

    fun randomStratified(sampleSize: Int, seed: Long?, minSubSample: Int?): Sampling {
        return RandomStratifiedSampling(sampleSize, seed, minSubSample)
    }

    private class NoneSampling : PointSampling {

        override val expressionText: String
            get() = "none"

        override fun isApplicable(population: DataFrame): Boolean {
            return false
        }

        override fun apply(population: DataFrame): DataFrame {
            return population
        }
    }
}
