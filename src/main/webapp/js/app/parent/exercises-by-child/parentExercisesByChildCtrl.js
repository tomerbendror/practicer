function parentExercisesByChildCtrl($scope, $location, msgBus, parentDataService) {
    $scope.errorDisplayed = false;

    parentDataService.getPracticesSummary()
        .then(function (practicesSummary) {
            $scope.practices = practicesSummary;
            $scope.createChildToPracticesMap();
        });

    parentDataService.getPracticesByChilds()
        .then(function (practicesByChilds) {
            $scope.practicesByChilds = practicesByChilds;
            $scope.createChildToPracticesMap();
        });

    $scope.createChildToPracticesMap = function() {
        if ($scope.practices && $scope.practicesByChilds) {
            var allPracticesMap = {};
            for (var t = 0; t < $scope.practices.length; t++) {
                var practice_ = $scope.practices[t];
                allPracticesMap[practice_.id] = practice_;
            }

            for (var j = 0; j < $scope.practicesByChilds.length; j++) {
                var childPracticesObj = $scope.practicesByChilds[j];
                childPracticesObj.fullName = childPracticesObj.childFirstName + ' ' + childPracticesObj.childLastName;
                var practicesIds = childPracticesObj.practicesIds;
                childPracticesObj.practices = [];
                for (var i = 0; i < practicesIds.length; i++) {
                    var practiceId = practicesIds[i];
                    var practice = allPracticesMap[practiceId];
                    childPracticesObj.practices.push(practice);
                }
            }
        }
    };

    msgBus.onMsg(PRACTICE_DELETED_EVENT, function(msg, deletedPracticeId) {
        for (var i=0; i<$scope.practicesByChilds.length; i++) {
            var childPracticesObj = $scope.practicesByChilds[i];
            var childPractices = childPracticesObj.practices;
            for (var j = 0; j < childPractices.length; j++) {
                var practice = childPractices[j];
                if (practice.id == deletedPracticeId) {
                    childPractices.splice(j, 1);
                    break;
                }
            }
        }
    }, $scope);

    $scope.addPractice = function(practiceType) {
        $location.path('/practice/' + practiceType);
    };

    setActiveMenu('#exercises-by-child-btn');
}