const webpack = require('webpack');

config.plugins = config.plugins || [];
config.plugins.push(new webpack.IgnorePlugin(/^(ws|text-encoding|abort-controller)$/));