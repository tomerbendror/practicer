function parentExercisesCtrl($scope, $location, parentDataService, msgBus) {
    $scope.errorDisplayed = false;

    parentDataService.getPracticesSummary()
        .then(function (data) {
            $scope.practices = data;
        });

    $scope.addPractice = function(practiceType) {
        $location.path('/practice/' + practiceType);
    };

    msgBus.onMsg(PRACTICE_DELETED_EVENT, function(msg, deletedPracticeId) {
        for (var i=0; i<$scope.practices.length; i++) {
            var practice = $scope.practices[i];
            if (practice.id == deletedPracticeId) {
                $scope.practices.splice(i, 1);
                return;
            }
        }
    }, $scope);

    setActiveMenu('#exercises-btn');
}