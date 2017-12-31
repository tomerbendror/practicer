function parentExercisesByGroupsCtrl($scope, $location, msgBus, parentDataService) {
    $scope.errorDisplayed = false;

    parentDataService.getPracticesSummary()
        .then(function (practicesSummary) {
            $scope.practices = practicesSummary;
            $scope.createGroupToPracticesMap();
        });

    parentDataService.getPracticesByGroups()
        .then(function (practicesByGroup) {
            $scope.practicesByGroup = practicesByGroup;
            $scope.createGroupToPracticesMap();
        });

    $scope.createGroupToPracticesMap = function() {
        if ($scope.practices && $scope.practicesByGroup) {
            var allPracticesMap = {};
            for (var t = 0; t < $scope.practices.length; t++) {
                var practice_ = $scope.practices[t];
                allPracticesMap[practice_.id] = practice_;
            }

            for (var j = 0; j < $scope.practicesByGroup.length; j++) {
                var groupPracticesObj = $scope.practicesByGroup[j];
                var practicesIds = groupPracticesObj.practicesIds;
                groupPracticesObj['practices'] = [];
                for (var i = 0; i < practicesIds.length; i++) {
                    var practiceId = practicesIds[i];
                    var practice = allPracticesMap[practiceId];
                    groupPracticesObj['practices'].push(practice);
                }
            }
        }
    };

    msgBus.onMsg(PRACTICE_DELETED_EVENT, function(msg, deletedPracticeId) {
        for (var i=0; i<$scope.practicesByGroup.length; i++) {
            var groupPracticesObj = $scope.practicesByGroup[i];
            var groupPractices = groupPracticesObj.practices;
            for (var j = 0; j < groupPractices.length; j++) {
                var practice = groupPractices[j];
                if (practice.id == deletedPracticeId) {
                    groupPractices.splice(j, 1);
                    break;
                }
            }
        }
    }, $scope);

    $scope.addPractice = function(practiceType) {
        $location.path('/practice/' + practiceType);
    };

    setActiveMenu('#exercises-by-group-btn');
}