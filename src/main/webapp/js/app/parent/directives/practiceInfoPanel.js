function practiceInfoPanel($timeout) {
    return {
        scope: {
            practice: "="
        },  // use a new isolated scope
        restrict: 'AE',
        replace: 'false',
        templateUrl: '/html/app/directives/practiceInfoPanel.html',
        controller: practiceInfoPanelCtrl,
        link: function ($scope, element) {
            $scope.element = $(element);
            $timeout(function(){onPracticeInfoLinked($scope.element, $scope);}, 0);
        }
    };
}

function practiceInfoPanelCtrl($scope, $location, contextService, practiceOperationService) {
    $scope.practiceDescription = getPracticeTypeStr($scope.practice.questionsType);
    $scope.userIsPracticeCreator = $scope.practice.creatorId == contextService.getUserId();

    $scope.openPracticePage = function() {
        $location.path('/practice/' + $scope.practice.id);
    };

    $scope.duplicatePractice = function() {
        practiceOperationService.duplicatePractice($scope.practice);
    };

    $scope.copyQuestionsToExistPractice = function() {
        practiceOperationService.copyQuestionsToExistPractice($scope.practice);
    };

    $scope.deletePractice = function() {
        practiceOperationService.deletePractice($scope.practice).then(function(deletedPracticeId) {
        });
    };
}

function onPracticeInfoLinked(element, $scope) {
}