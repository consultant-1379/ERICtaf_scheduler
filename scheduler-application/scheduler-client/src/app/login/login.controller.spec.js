describe('controllers', function() {
    var scope;

    beforeEach(module('app.login'));

    beforeEach(inject(function($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should define more than 5 awesome things', inject(function() {
    }));
});
