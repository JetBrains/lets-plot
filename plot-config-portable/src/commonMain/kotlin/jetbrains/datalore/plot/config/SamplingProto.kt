package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.sampling.Samplings
import jetbrains.datalore.plot.builder.sampling.Samplings.PICK
import jetbrains.datalore.plot.builder.sampling.Samplings.RANDOM
import jetbrains.datalore.plot.builder.sampling.Samplings.RANDOM_GROUP
import jetbrains.datalore.plot.builder.sampling.Samplings.RANDOM_STRATIFIED
import jetbrains.datalore.plot.builder.sampling.Samplings.SYSTEMATIC
import jetbrains.datalore.plot.builder.sampling.Samplings.SYSTEMATIC_GROUP
import jetbrains.datalore.plot.builder.sampling.Samplings.VERTEX_DP
import jetbrains.datalore.plot.builder.sampling.Samplings.VERTEX_VW
import jetbrains.datalore.plot.config.Option.Sampling.MIN_SUB_SAMPLE
import jetbrains.datalore.plot.config.Option.Sampling.N
import jetbrains.datalore.plot.config.Option.Sampling.SEED

internal object SamplingProto {

    fun createSampling(name: String, options: Map<*, *>): Sampling {
        val opts = OptionsAccessor.over(options)
        return when (name) {
            RANDOM -> Samplings.random(opts.getInteger(N)!!, opts.getLong(SEED))
            PICK -> Samplings.pick(opts.getInteger(N)!!)
            SYSTEMATIC -> Samplings.systematic(opts.getInteger(N)!!)
            RANDOM_GROUP -> Samplings.randomGroup(opts.getInteger(N)!!, opts.getLong(SEED))
            SYSTEMATIC_GROUP -> Samplings.systematicGroup(opts.getInteger(N)!!)
            RANDOM_STRATIFIED -> Samplings.randomStratified(opts.getInteger(N)!!, opts.getLong(SEED), opts.getInteger(MIN_SUB_SAMPLE))
            VERTEX_VW -> Samplings.vertexVw(opts.getInteger(N)!!)
            VERTEX_DP -> Samplings.vertexDp(opts.getInteger(N)!!)

            else -> throw IllegalArgumentException("Unknown sampling method: '$name'")
        }
    }
}
