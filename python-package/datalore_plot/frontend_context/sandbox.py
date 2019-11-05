#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
src = """\
(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'datalore-plot-base-portable'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'datalore-plot-base-portable'.");
    }
    root['datalore-plot-base-portable'] = factory(typeof this['datalore-plot-base-portable'] === 'undefined' ? {} : this['datalore-plot-base-portable'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init;
  var ensureNotNull = Kotlin.ensureNotNull;
  var throwCCE = Kotlin.throwCCE;
"""

import re

lib = "my_lib"
res = re.sub(r'define\(\[', "define('{module}', [".format(module=lib), src)
print(res)

