module.exports = function (config) {
    config.set({
            frameworks: ['mocha', 'commonjs'],
            reporters: ['mocha'],
            files: [
                'build/classes/kotlin/js/main/*.js',
                'build/classes/kotlin/js/test/*.js',
                'build/node_modules/*.js'
            ],
            exclude: [],
            colors: true,
            autoWatch: false,
            browsers: [
                'PhantomJS'
                // , 'Chrome'
            ],
            singleRun: true,

            preprocessors: {
                '**/*.js': ['commonjs']
            }
        }
    )
};
