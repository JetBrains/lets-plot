const webpack = require('webpack');

config.plugins = config.plugins || [];
config.plugins.push(new webpack.IgnorePlugin({ resourceRegExp: /^(ws|text-encoding|abort-controller|crypto)$/ }));