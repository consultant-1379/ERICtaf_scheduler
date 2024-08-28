'use strict';

var gulp = require('gulp');
var gutil = require('gulp-util');
var wrench = require('wrench');

var options = {
    src: 'src',
    target: 'target',
    dist: 'target/client-dist',
    tmp: '.tmp',
    errorHandler: function(title) {
        return function(err) {
            gutil.log(gutil.colors.red('[' + title + ']'), err.toString());
            this.emit('end');
        };
    },
    wiredep: {
        directory: 'bower_components',
        exclude: [
            /jquery/,
            /bootstrap\.js/
        ]
    }
};

wrench.readdirSyncRecursive('./gulp').filter(function(file) {
    return (/\.(js|coffee)$/i).test(file);
}).map(function(file) {
    require('./gulp/' + file)(options);
});

gulp.task('default', ['clean'], function() {
    gulp.start('package');
});
