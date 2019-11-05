#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['sampling_random',
           'sampling_random_stratified',
           'sampling_pick',
           'sampling_systematic',
           'sampling_group_random',
           'sampling_group_systematic',
           'sampling_vertex_vw',
           'sampling_vertex_dp']


def sampling_random(n, seed=None):
    return _sampling('random', n=n, seed=seed)


def sampling_random_stratified(n, seed=None, min_subsample=None):
    return _sampling('random_stratified', n=n, seed=seed, min_subsample=min_subsample)


def sampling_pick(n):
    return _sampling('pick', n=n)


def sampling_systematic(n):
    return _sampling('systematic', n=n)


def sampling_group_systematic(n):
    return _sampling('group_systematic', n=n)


def sampling_group_random(n, seed=None):
    return _sampling('group_random', n=n, seed=seed)


def sampling_vertex_vw(n):
    return _sampling('vertex_vw', n=n)


def sampling_vertex_dp(n):
    return _sampling('vertex_dp', n=n)


def _sampling(name, **kwargs):
    return FeatureSpec('sampling', name, **kwargs)
