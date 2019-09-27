const path = require('path');

const demoWepPath = path.resolve(__dirname, 'build', 'demoWeb');
const libPath = path.resolve(demoWepPath, 'lib');
const distPath = path.resolve(demoWepPath, 'dist');

module.exports = {
    entry: `${libPath}/visualization-plot-config.js`,
    output: {
        library: 'datalorePlot',
        filename: 'datalore-plot.js',
        path: distPath,
        libraryTarget: 'umd',
        globalObject: 'this'
    },
    resolve: {
        modules: [libPath, 'node_modules']
    },
    mode: 'production'
};