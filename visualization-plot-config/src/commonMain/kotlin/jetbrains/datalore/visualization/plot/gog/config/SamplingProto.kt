package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.builder.sampling.Sampling
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.PICK
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.RANDOM
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.RANDOM_GROUP
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.RANDOM_STRATIFIED
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.SYSTEMATIC
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.SYSTEMATIC_GROUP
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.VERTEX_DP
import jetbrains.datalore.visualization.plot.builder.sampling.Samplings.VERTEX_VW
import jetbrains.datalore.visualization.plot.gog.config.Option.Sampling.MIN_SUB_SAMPLE
import jetbrains.datalore.visualization.plot.gog.config.Option.Sampling.N
import jetbrains.datalore.visualization.plot.gog.config.Option.Sampling.SEED

internal object SamplingProto {

    fun createSampling(name: String, options: Map<*, *>): Sampling {
        val opts = OptionsAccessor.over(options)
        when (name) {
            RANDOM -> return Samplings.random(opts.getInteger(N)!!, opts.getLong(SEED))
            PICK -> return Samplings.pick(opts.getInteger(N)!!)
            SYSTEMATIC -> return Samplings.systematic(opts.getInteger(N)!!)
            RANDOM_GROUP -> return Samplings.randomGroup(opts.getInteger(N)!!, opts.getLong(SEED))
            SYSTEMATIC_GROUP -> return Samplings.systematicGroup(opts.getInteger(N)!!)
            RANDOM_STRATIFIED -> return Samplings.randomStratified(opts.getInteger(N)!!, opts.getLong(SEED), opts.getInteger(MIN_SUB_SAMPLE))
            VERTEX_VW -> return Samplings.vertexVw(opts.getInteger(N)!!)
            VERTEX_DP -> return Samplings.vertexDp(opts.getInteger(N)!!)

            else -> throw IllegalArgumentException("Unknown sampling method: '$name'")
        }
    }
}
