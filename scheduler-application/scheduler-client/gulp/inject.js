'use strict';

var gulp = require('gulp');

var $ = require('gulp-load-plugins')();

var wiredep = require('wiredep').stream;

module.exports = function(options) {
    var injectTask = function() {
        var injectStyles = gulp.src([
            options.tmp + '/serve/app/**/*.css',
            '!' + options.tmp + '/serve/app/vendor.css'
        ], {read: false});

        var injectScripts = gulp.src([
            options.tmp + '/serve/scripts/**/*.js',
            '!' + options.tmp + '/serve/scripts/**/*.spec.js',
            '!' + options.tmp + '/serve/scripts/**/*.mock.js'
        ])
            .pipe($.angularFilesort()).on('error', options.errorHandler('AngularFilesort'));

        var injectOptions = {
            ignorePath: [options.src, options.tmp + '/serve'],
            addRootSlash: false
        };

        return gulp.src(options.src + '/*.html')
            .pipe($.inject(injectStyles, injectOptions))
            .pipe($.inject(injectScripts, injectOptions))
            .pipe(wiredep(options.wiredep))
            .pipe(gulp.dest(options.tmp + '/serve'));
    };

    gulp.task('inject', ['scripts', 'styles'], injectTask);
    gulp.task('inject:dist', ['scripts:dist', 'styles'], injectTask);
};
