const commonConfig = require('./webpack.common.config');

module.exports = {
    entry: commonConfig.entry,
    output: Object.assign(commonConfig.output, {
        filename: 'datalore-plot.js'
    }),
    resolve: commonConfig.resolve,
    mode: 'development',
    plugins: commonConfig.plugins
};