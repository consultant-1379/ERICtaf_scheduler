'use strict';

var gulp = require('gulp');
var $ = require('gulp-load-plugins')();

module.exports = function(options) {
    gulp.task('package', ['build'], function() {
        var tgzFiles = [
            options.dist + '/**',
            '!' + options.dist + '/app{,/**}'
        ];

        return gulp.src(tgzFiles)
            .pipe($.tar('scheduler-client.tar'))
            .pipe($.gzip())
            .pipe(gulp.dest(options.target));
    });
};
