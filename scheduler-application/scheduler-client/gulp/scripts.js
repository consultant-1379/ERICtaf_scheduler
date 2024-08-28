'use strict';

var gulp = require('gulp');
var browserSync = require('browser-sync');

var $ = require('gulp-load-plugins')();

module.exports = function(options) {
    var scriptsTask = function() {
        var noop = function() {
        };

        return gulp.src(options.src + '/app/**/*.js')
            .pipe($.jscs()).on('error', noop)
            .pipe($.jshint())
            .pipe($.jscsStylish.combineWithHintResults())
            .pipe($.jshint.reporter('jshint-stylish'))
            .pipe($.wrap('(function(){\'use strict\';<%= contents %>\n}());'))
            .pipe(gulp.dest(options.tmp + '/serve/scripts'))
            .pipe($.size());
    };

    gulp.task('scripts', function() {
        return scriptsTask().pipe(browserSync.reload({stream: true}));
    });

    gulp.task('scripts:dist', function() {
        return scriptsTask().pipe($.jshint.reporter('fail'));
    });
};
