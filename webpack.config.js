//var debug = process.env.NODE_ENV !== "production";
var webpack = require('webpack');
var path = require('path');

module.exports = {
    context: path.join(__dirname, "src/main/webapp"),
    //devtool: debug ? "inline-sourcemap" : null,
    entry: {
        login: "./js//login/Login.js",
        np: "./js/usermgmt/NamoduPustakamApp.js",
        signup: "./js/signup/Signup.js"
    },
    module: {
        loaders: [
            {
                test: /\.jsx?$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel-loader',
                query: {
                    presets: ['react', 'es2015', 'stage-0'],
                    plugins: ['react-html-attrs', 'transform-decorators-legacy', 'transform-class-properties'],
                }
            },
            {test: /\.css$/, exclude: /\.useable\.css$/, loader: "style!css"},
            {test: /\.useable\.css$/, loader: "style/useable!css"},
            {test: /\.scss$/, loaders: ["style", "css", "sass"]},
            {
                test: /\.png$/,
                loader: "url-loader?limit=100000"
            },
            {
                test: /\.jpg$/,
                loader: "file-loader"
            },
            {
                test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url?limit=10000&mimetype=application/font-woff'
            },
            {
                test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url?limit=10000&mimetype=application/octet-stream'
            },
            {
                test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file'
            },
            {
                test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url?limit=10000&mimetype=image/svg+xml'
            }
        ]
    },
    output: {
        path: __dirname + "/src/main/resources/static/js",
        filename: "[name].app.min.js"
    },
    plugins: [
        new webpack.ProvidePlugin({
            Promise: 'imports?this=>global!exports?global.Promise!es6-promise',
            fetch: 'imports?this=>global!exports?global.fetch!whatwg-fetch'
        })
    ]

    // plugins: debug ? [new webpack.ProvidePlugin({
    //     'fetch': 'imports?this=>global!exports?global.fetch!whatwg-fetch'
    // })] : [
    //     new webpack.optimize.DedupePlugin(),
    //     new webpack.optimize.OccurenceOrderPlugin(),
    //     new webpack.optimize.UglifyJsPlugin({mangle: false, sourcemap: false}),
    // ],
};