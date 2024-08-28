angular
    .module('app.schedules')
    .service('TeamsService', TeamsService);

function TeamsService(Teams) {
    this.getAllTeams = getAllTeams;
    var teams = [];

    function getAllTeams(callback) {
        var buildPromise = Teams.getAllTeams();
        buildPromise.then(callback);
        teams = buildPromise;
        return buildPromise;
    }
}
