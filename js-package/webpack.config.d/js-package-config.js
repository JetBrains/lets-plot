config.output = config.output || {};
config.output.library = "LetsPlot";
config.output.libraryTarget = "window";
config.output.globalObject = "window";

config.node = config.node || {};
config.node.Buffer = false;

const webpack = require('webpack');

config.plugins = config.plugins || [];
config.plugins.push(new webpack.IgnorePlugin(/^(ws|text-encoding|abort-controller|buffer|node-fetch)$/));