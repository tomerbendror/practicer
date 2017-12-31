function childRecentPracticesController($scope, dataService, msgBus) {
    msgBus.emitMsg('navigationEvent', 'תורגל לאחרונה');

    $scope.createPracticesList = function () {
        $scope.practicesList = [];
        if ($scope.recentPracticesIds && $scope.allPractices) {
            for(var i=0; i<$scope.recentPracticesIds.length; i++) {
                var practiceId = $scope.recentPracticesIds[i];
                for(var j=0; j<$scope.allPractices.length; j++) {
                    var practiceSummary = $scope.allPractices[j];
                    if (practiceSummary.id == practiceId) {
                        if (practiceSummary.questionsType == 'DICTATION') {
                            practiceSummary.questionsTypeStr = 'הכתבה';
                        } else if (practiceSummary.questionsType == 'TRANSLATION') {
                            practiceSummary.questionsTypeStr = 'תרגום';
                        } else if (practiceSummary.questionsType == 'MATH') {
                            practiceSummary.questionsTypeStr = 'חשבון';
                        }
                        $scope.practicesList.push(practiceSummary);
                        break;
                    }
                }
            }
        }
    };

    dataService.getRecentPracticesIds()
        .then(function (practicesIds) {
            $scope.recentPracticesIds = practicesIds;
            $scope.createPracticesList();
        });

    dataService.getChildSummary()
        .then(function (result) {
            $scope.allPractices = result.practicesSummary;
            $scope.createPracticesList();

            //var practices = result.practicesSummary;
            //$scope.groupIdToPracticesMapOriginal = {};
            //
            //for (var i=0; i<practices.length; i++) {
            //    var practice = practices[i];
            //    var groupPractices = $scope.groupIdToPracticesMapOriginal[practice.groupId];
            //    if( typeof groupPractices === 'undefined' || groupPractices === null ){
            //        groupPractices = [];
            //        $scope.groupIdToPracticesMapOriginal[practice.groupId] = groupPractices;
            //    }
            //
            //    if (practice.questionsType == 'DICTATION') {
            //        practice.questionsTypeStr = 'הכתבה';
            //    } else if (practice.questionsType == 'TRANSLATION') {
            //        practice.questionsTypeStr = 'תרגום';
            //    } else if (practice.questionsType == 'MATH') {
            //        practice.questionsTypeStr = 'חשבון';
            //    }
            //
            //    groupPractices.push(practice);
            //}
            //$scope.groupIdToPracticesMap = $scope.groupIdToPracticesMapOriginal;
        });
}