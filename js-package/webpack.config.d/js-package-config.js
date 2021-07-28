config.output = config.output || {};
config.output.library = "LetsPlot";
config.output.libraryTarget = "window";
config.output.globalObject = "window";

const webpack = require('webpack');

config.plugins = config.plugins || [];
config.plugins.push(new webpack.IgnorePlugin({ resourceRegExp: /^(ws|text-encoding|abort-controller|buffer|node-fetch|crypto)$/ }));