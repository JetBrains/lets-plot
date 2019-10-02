const path = require('path');

const buildPath = path.resolve(__dirname, 'build');
const libPath = path.resolve(buildPath, 'js');
const distPath = path.resolve(buildPath, 'dist');

module.exports = {
    entry: `${libPath}/visualization-plot-config.js`,
    output: {
        library: 'datalorePlot',
        filename: 'datalore-plot.min.js',
        path: distPath,
        libraryTarget: 'window',
        globalObject: 'window'
    },
    resolve: {
        modules: [libPath, 'node_modules']
    },
    mode: 'production'
};