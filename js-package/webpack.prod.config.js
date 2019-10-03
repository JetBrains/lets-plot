const commonConfig = require('./webpack.common.config');

module.exports = {
    entry: commonConfig.entry,
    output: Object.assign(commonConfig.output, {
        filename: 'datalore-plot.min.js'
    }),
    resolve: commonConfig.resolve,
    mode: 'production'
};