'use strict';

var httpProxy = require('http-proxy');
var chalk = require('chalk');

/*
 * Location of your backend server
 */
var proxyTarget = 'http://localhost:8040/';
var proxy = httpProxy.createProxyServer({
    target: proxyTarget
});

proxy.on('error', function(error, req, res) {
    res.writeHead(500, {
        'Content-Type': 'text/plain'
    });

    console.error(chalk.red('[Proxy]'), error);
});

/*
 * The proxy middleware is an Express middleware added to BrowserSync to
 * handle backend request and proxy them to your backend.
 */
function proxyMiddleware(req, res, next) {
    /*
     * This test is the switch of each request to determine if the request is
     * for a static file to be handled by BrowserSync or a backend request to proxy.
     */
    if (/^\/api\//.test(req.url)) {
        proxy.web(req, res);
    } else {
        next();
    }
}

module.exports = function() {
    return [proxyMiddleware];
};
