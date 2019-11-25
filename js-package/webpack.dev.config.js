/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

const commonConfig = require('./webpack.common.config');

module.exports = {
    entry: commonConfig.entry,
    output: Object.assign(commonConfig.output, {
        filename: 'lets-plot.js'
    }),
    resolve: commonConfig.resolve,
    mode: 'development',
    plugins: commonConfig.plugins
};